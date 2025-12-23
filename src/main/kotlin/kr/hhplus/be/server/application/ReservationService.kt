package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.enums.ReservationStatus
import kr.hhplus.be.server.infrastructure.persistence.ConcertSeatJpaRepo
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class ReservationService(
    private val concertSeatRepo: ConcertSeatJpaRepo,
    private val reservationRepo: ReservationRepo,
    private val redissonClient: RedissonClient
) {
    object LockKey {
        fun seat(seatId: String) = "lock:seat:$seatId"
    }

    /**
     * 좌석 임시점유 예약 생성
     */

    fun reserve(
        seatId: String, userId: String
    ): Reservation {

        // 좌석 예약을 위한 락 획득
        val lock = redissonClient.getLock(LockKey.seat(seatId))

        try {
            // 1. 락 획득 시도 (최대 5초 대기, 획득 후 10초간 유지)
            val available = lock.tryLock(5, 10, TimeUnit.SECONDS)

            if (!available) {
                throw IllegalStateException("현재 예약이 진행 중인 좌석입니다. 잠시 후 다시 시도해주세요.")
            }

            // 2. 실제 비즈니스 로직 실행
            return proceedReservation(seatId, userId)

        } catch (e: InterruptedException) {
            throw RuntimeException("예약 중 오류가 발생했습니다.")
        } finally {
            // 3. 락 해제 (락을 보유한 경우에만)
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }

    @Transactional
    private fun proceedReservation(
        seatId: String, userId: String
    ): Reservation {
        // 예약 가능한지 확인
        val reserveTargetSeat = concertSeatRepo.findByUuid(
            concertSeatId = seatId
        ) ?: throw IllegalArgumentException("좌석을 찾을 수 없습니다.")

        if (reserveTargetSeat.status != ReservationStatus.AVAILABLE) {
            throw IllegalStateException("이미 선택된 좌석입니다.")
        }

        // 임시 좌석점유 예약 생성
        val temporaryReservation = Reservation.create(
            scheduleId = reserveTargetSeat.concertScheduleId,
            seatId = reserveTargetSeat.uuid,
            userId = userId
        )

        reserveTargetSeat.reserve() // 좌석 상태 변경

        return reservationRepo.save(temporaryReservation)
    }

    // 예약 확정
    @Transactional
    fun confirmReservation(reservationId: String): Reservation {
        val findReservation = reservationRepo.findByUuid(reservationId)
            ?: throw IllegalStateException("예약을 찾을 수 없습니다.")

        findReservation.confirm()
        return reservationRepo.save(findReservation)
    }
}
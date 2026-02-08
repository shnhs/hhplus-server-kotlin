package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.Reservation
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class ReservationService(
    private val reservationProcessor: ReservationProcessor,
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
            return reservationProcessor.proceedReservation(seatId, userId)

        } catch (e: InterruptedException) {
            throw RuntimeException("예약 중 오류가 발생했습니다.")
        } finally {
            // 3. 락 해제 (락을 보유한 경우에만)
            if (lock.isHeldByCurrentThread) {
                lock.unlock()
            }
        }
    }


    // 예약 확정
    fun confirmReservation(reservationId: String): Reservation {
        val confirmedReservation = reservationProcessor.processConfirm(
            reservationId = reservationId
        )
        return confirmedReservation
    }
}
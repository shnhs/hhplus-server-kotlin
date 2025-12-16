package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.enums.ReservationStatus
import kr.hhplus.be.server.infrastructure.persistence.ConcertSeatJpaRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val concertSeatRepo: ConcertSeatJpaRepo,
    private val reservationRepo: ReservationRepo
) {
    /**
     * 좌석 임시점유 예약 생성
     */
    @Transactional
    fun reserve(
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
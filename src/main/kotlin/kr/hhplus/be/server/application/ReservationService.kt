package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ReservationService(
    private val reservationRepo: ReservationRepo
) {
    // 좌석예약 요청
    @Transactional
    fun reserve(
        concertId: String, scheduleId: String, seatNumber: Int, userId: String
    ): Reservation {
        // 예약 가능한지 확인
        val reservationTarget = reservationRepo.findByConcertIdAndScheduleIdAndSeatNumber(
            concertId = concertId, scheduleId = scheduleId, seatNumber = seatNumber
        )
        if (!reservationTarget.isAvailable()) {
            throw IllegalStateException("이미 선택된 좌석입니다.")
        }

        // 예약임시 점유
        reservationTarget.reserve(userId = userId)

        return reservationRepo.save(reservationTarget)
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
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
    fun reserve(
        concertId: String, scheduleId: String, seatNumber: Int, userId: String
    ): Reservation {
        // 예약 가능한지 확인
        val isReserved = reservationRepo.findByConcertIdAndScheduleIdAndSeatNumber(
            concertId = concertId, scheduleId = scheduleId, seatNumber = seatNumber
        )
        if (isReserved != null) {
            throw IllegalStateException("이미 예약된 좌석입니다.")
        }

        // 예약생성
        val createdReservation = Reservation.create(
            concertId = concertId, scheduleId = scheduleId, seatNumber = seatNumber, userId = userId
        )

        return reservationRepo.save(createdReservation)
    }

    // 예약 확정
    fun confirmReservation(reservationId: String): Reservation {
        val findReservation = reservationRepo.findByUuid(reservationId)
            ?: throw IllegalStateException("예약을 찾을 수 없습니다.")

        findReservation.confirm()
        return reservationRepo.save(findReservation)
    }
}
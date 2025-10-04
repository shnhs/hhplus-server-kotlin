package kr.hhplus.be.server.domain.repo

import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.model.ReservationStatus

interface ReservationRepo {
    fun save(reservation: Reservation): Reservation

    fun findByConcertIdAndScheduleIdAndSeatNumber(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): Reservation?

    fun findByUuid(uuid: String): Reservation?

    fun findExpiredReservation(): List<Reservation>

    fun findByScheduleIdAndStatusIn(
        scheduleId: String, status: List<ReservationStatus>
    ): List<Reservation>
}
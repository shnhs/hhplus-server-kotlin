package kr.hhplus.be.server.domain.repo


import kr.hhplus.be.server.domain.model.Reservation

interface ReservationRepo {
    fun save(reservation: Reservation): Reservation

    fun findByUuid(uuid: String): Reservation?

    fun findExpiredReservation(): List<Reservation>

    fun findBySeatId(seatId: String): Reservation?

    fun deleteAll()
}
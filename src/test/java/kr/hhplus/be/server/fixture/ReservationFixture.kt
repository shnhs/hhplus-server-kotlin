package kr.hhplus.be.server.fixture

import jakarta.persistence.EntityManager
import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import org.springframework.stereotype.Component

@Component
class ReservationFixture(
    private val reservationRepo: ReservationRepo,
    private val em: EntityManager
) {
    fun createAvailableSeats(
        concertId: String,
        scheduleId: String,
        seatCount: Int
    ) {
        (1..seatCount).forEach { seatNumber ->
            val create = Reservation.create(
                concertId = concertId,
                scheduleId = scheduleId,
                seatNumber = seatNumber
            )
            reservationRepo.save(create)
        }
    }

    fun createReservedSeat(
        concertId: String,
        scheduleId: String,
        seatNumber: Int,
        userId: String
    ): Reservation {
        val reservation = Reservation.create(
            concertId = concertId,
            scheduleId = scheduleId,
            seatNumber = seatNumber
        )
        reservation.reserve(userId)
        return reservationRepo.save(reservation)
    }
}
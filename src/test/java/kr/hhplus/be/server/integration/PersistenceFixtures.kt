package kr.hhplus.be.server.integration

import kr.hhplus.be.server.entity.ConcertSeatEntity
import kr.hhplus.be.server.enums.ReservationStatus
import kr.hhplus.be.server.infrastructure.persistence.ConcertSeatJpaRepo
import org.springframework.stereotype.Component

@Component
class PersistenceFixtures(
    private val concertSeatRepo: ConcertSeatJpaRepo,
) {
    fun createAvailableSeats(
        concertId: String,
        scheduleId: String,
        seatCount: Int
    ): List<String> {
        return (1..seatCount).map { seatNumber ->
            val create = ConcertSeatEntity(
                concertId,
                scheduleId,
                seatNumber,
                ReservationStatus.AVAILABLE
            )
            concertSeatRepo.save(create).uuid  // 저장 후 uuid 반환
        }
    }
}
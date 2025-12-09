package kr.hhplus.be.server.infrastructure.persistence

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.entity.ConcertSeatEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.stereotype.Repository

@Repository
interface ConcertSeatJpaRepo : JpaRepository<ConcertSeatEntity, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByUuid(concertSeatId: String): ConcertSeatEntity?

    fun findByConcertScheduleId(concertScheduleId: String): List<ConcertSeatEntity>
}
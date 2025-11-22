package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.entity.ConcertScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConcertScheduleJpaRepo : JpaRepository<ConcertScheduleEntity, Long> {
    fun findByUuid(uuid: String): ConcertScheduleEntity?

    fun findByConcertId(concertId: String): List<ConcertScheduleEntity>
}
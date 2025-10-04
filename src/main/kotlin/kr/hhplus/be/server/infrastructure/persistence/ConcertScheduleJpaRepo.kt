package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.entity.ConcertScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface ConcertScheduleJpaRepo : JpaRepository<ConcertScheduleEntity, Long> {
    fun findByUuid(uuid: String): ConcertScheduleEntity?

    fun findByConcertId(concertId: String): List<ConcertScheduleEntity>
}
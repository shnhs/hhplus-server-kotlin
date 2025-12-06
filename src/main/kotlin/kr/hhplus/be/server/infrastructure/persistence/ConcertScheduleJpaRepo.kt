package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.entity.ConcertScheduleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface ConcertScheduleJpaRepo : JpaRepository<ConcertScheduleEntity, Long> {
    fun findByUuid(uuid: String): ConcertScheduleEntity?

    fun findByConcertId(concertId: String): List<ConcertScheduleEntity>

    // 특정 콘서트의 특정 날짜의 모든 좌석 조회
    fun findByConcertIdAndConcertDate(
        concertId: String,
        concertDate: LocalDateTime
    ): List<ConcertScheduleEntity>

    // 또는 날짜 범위로 조회
    fun findByConcertIdAndConcertDateBetween(
        concertId: String,
        startDate: LocalDateTime,
        endDate: LocalDateTime
    ): List<ConcertScheduleEntity>
}
package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.entity.ConcertEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ConcertJpaRepo : JpaRepository<ConcertEntity, Long> {
    fun findByUuid(uuid: String): ConcertEntity?
    fun findByUuidIn(uuids: Collection<String>): List<ConcertEntity>
}
package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.domain.model.ReservationToken
import kr.hhplus.be.server.domain.repo.ReservationTokenRepo
import org.springframework.data.jpa.repository.JpaRepository

interface SpringReservationTokenJpa : JpaRepository<ReservationTokenEntity, Long> {
    fun findByUuid(uuid: String): ReservationTokenEntity
    fun findByUserId(userId: String): ReservationTokenEntity?
}


class ReservationTokenJpaRepo(
    private val jpa: SpringReservationTokenJpa
) : ReservationTokenRepo {
    override fun save(reservationToken: ReservationToken): ReservationToken {
        val entity = toEntity(reservationToken)
        jpa.save(entity)
        return reservationToken
    }

    override fun findByUuid(uuid: String): ReservationToken {
        val entity = jpa.findByUuid(uuid)
        return toDomain(entity)
    }

    override fun findByUserId(userId: String): ReservationToken? {
        val entity = jpa.findByUserId(userId)
        return entity?.let { toDomain(it) }
    }

    override fun delete(reservationToken: ReservationToken): ReservationToken {
        jpa.delete(toEntity(reservationToken))
        return reservationToken
    }

    private fun toEntity(reservationToken: ReservationToken): ReservationTokenEntity {
        return ReservationTokenEntity().apply {
            uuid = reservationToken.tokenId
            userId = reservationToken.userId
            createdAt = reservationToken.createdAt
        }
    }

    private fun toDomain(entity: ReservationTokenEntity): ReservationToken {
        return ReservationToken.createWithUuid(
            tokenId = entity.userId,
            userId = entity.userId,
            createdAt = entity.createdAt
        )
    }
}
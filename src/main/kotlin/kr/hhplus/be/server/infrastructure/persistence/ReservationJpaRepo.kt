package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

interface SpringReservationJpa : JpaRepository<ReservationEntity, Long> {

    fun findByUuid(uuid: String): ReservationEntity?

    fun findBySeatId(seatId: String): ReservationEntity?

    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'PENDING' AND r.reservedAt < :expiredTime")
    fun findExpiredReservations(expiredTime: LocalDateTime): List<ReservationEntity>
}

@Repository
class ReservationJpaRepo(private val jpa: SpringReservationJpa) : ReservationRepo {
    override fun save(reservation: Reservation): Reservation {
        // UUID로 기존 엔티티 조회
        val existingEntity = jpa.findByUuid(reservation.getUuid())

        if (existingEntity != null) {
            // 기존엔티티 업데이트
            existingEntity.userId = reservation.getUserId()
            existingEntity.status = reservation.getStatus()
            existingEntity.reservedAt = reservation.getReservedAt()
            jpa.saveAndFlush(existingEntity)
            return reservation
        } else {
            // 새 엔티티 저장
            val entity = toEntity(reservation)
            jpa.saveAndFlush(entity)
            return reservation
        }
    }

    override fun findByUuid(uuid: String): Reservation? {
        val entity = jpa.findByUuid(uuid)
        return entity?.let { toDomain(it) }
    }

    override fun findExpiredReservation(): List<Reservation> {
        TODO("Not yet implemented")
    }

    override fun findBySeatId(seatId: String): Reservation? {
        val entity = jpa.findBySeatId(seatId)
        return entity?.let { toDomain(it) }
    }

    override fun deleteAll() {
        jpa.deleteAll();
    }

    // Domain -> JPA Entity 매핑 (Infrastructure 전용)
    private fun toEntity(reservation: Reservation): ReservationEntity {
        return ReservationEntity().apply {
            uuid = reservation.getUuid()
            scheduleId = reservation.getScheduleId()
            seatId = reservation.getSeatId()
            userId = reservation.getUserId()
            reservedAt = reservation.getReservedAt()
            status = reservation.getStatus()
        }
    }

    // JPA Entity -> Domain 매핑
    private fun toDomain(entity: ReservationEntity): Reservation {
        // UUID 기반으로 도메인 객체 재구성
        return Reservation.createWithUuid(
            uuid = entity.uuid,
            scheduleId = entity.scheduleId,
            seatId = entity.seatId,
            userId = entity.userId,
            reservedAt = entity.reservedAt,
            status = entity.status
        )
    }
}
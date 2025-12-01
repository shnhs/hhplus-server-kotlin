package kr.hhplus.be.server.infrastructure.persistence

import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.enums.ReservationStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

interface SpringReservationJpa : JpaRepository<ReservationEntity, Long> {
    // 락 없는 일반 조회 (테스트, 단순 확인용)
    fun findTopByConcertIdAndScheduleIdAndSeatNumber(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): ReservationEntity?


    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByConcertIdAndScheduleIdAndSeatNumber(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): ReservationEntity

    fun findByUuid(uuid: String): ReservationEntity?

    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'PENDING' AND r.reservedAt < :expiredTime")
    fun findExpiredReservations(expiredTime: LocalDateTime): List<ReservationEntity>

    fun findByScheduleIdAndStatusIn(scheduleId: String, status: List<ReservationStatus>): List<ReservationEntity>
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

    // 락없는 일반조회
    override fun findByConcertIdAndScheduleIdAndSeatNumberWithoutLock(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): Reservation? {
        val entity = jpa.findTopByConcertIdAndScheduleIdAndSeatNumber(
            concertId, scheduleId, seatNumber
        )
        return entity?.let { toDomain(it) }

    }

    // Lock 걸고 조회
    override fun findByConcertIdAndScheduleIdAndSeatNumber(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): Reservation {
        val entity = jpa.findByConcertIdAndScheduleIdAndSeatNumber(
            concertId,
            scheduleId,
            seatNumber
        )
        return toDomain(entity)
    }

    override fun findByUuid(uuid: String): Reservation? {
        val entity = jpa.findByUuid(uuid)
        return entity?.let { toDomain(it) }
    }

    override fun findExpiredReservation(): List<Reservation> {
        TODO("Not yet implemented")
    }

    override fun deleteAll() {
        jpa.deleteAll();
    }

    // Domain -> JPA Entity 매핑 (Infrastructure 전용)
    private fun toEntity(reservation: Reservation): ReservationEntity {
        return ReservationEntity().apply {
            uuid = reservation.getUuid()
            concertId = reservation.getConcertId()
            scheduleId = reservation.getScheduleId()
            seatNumber = reservation.getSeatNumber()
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
            concertId = entity.concertId,
            scheduleId = entity.scheduleId,
            seatNumber = entity.seatNumber,
            userId = entity.userId,
            reservedAt = entity.reservedAt,
            status = entity.status
        )
    }
}
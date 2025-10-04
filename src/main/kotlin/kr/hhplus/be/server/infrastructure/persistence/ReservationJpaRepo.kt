package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.model.ReservationStatus
import kr.hhplus.be.server.domain.repo.ReservationRepo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface SpringReservationJpa : JpaRepository<ReservationEntity, Long> {
    fun findByConcertIdAndScheduleIdAndSeatNumber(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): ReservationEntity?

    fun findByUuid(uuid: String): ReservationEntity?

    @Query("SELECT r FROM ReservationEntity r WHERE r.status = 'PENDING' AND r.reservedAt < :expiredTime")
    fun findExpiredReservations(expiredTime: LocalDateTime): List<ReservationEntity>

    fun findByScheduleIdAndStatusIn(scheduleId: String, status: List<ReservationStatus>): List<ReservationEntity>
}

class ReservationJpaRepo(private val jpa: SpringReservationJpa) : ReservationRepo {
    override fun save(reservation: Reservation): Reservation {
        val entity = toEntity(reservation)
        jpa.save(entity)
        return reservation
    }

    override fun findByConcertIdAndScheduleIdAndSeatNumber(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): Reservation? {
        val entity = jpa.findByConcertIdAndScheduleIdAndSeatNumber(
            concertId,
            scheduleId,
            seatNumber
        )
        return entity?.let { toDomain(it) }
    }

    override fun findByUuid(uuid: String): Reservation? {
        val entity = jpa.findByUuid(uuid)
        return entity?.let { toDomain(it) }
    }

    override fun findExpiredReservation(): List<Reservation> {
        TODO("Not yet implemented")
    }

    override fun findByScheduleIdAndStatusIn(
        scheduleId: String, status: List<ReservationStatus>
    ): List<Reservation> {
        val reservations = jpa.findByScheduleIdAndStatusIn(
            scheduleId, status
        )

        return reservations.map { toDomain(it) }.toList()
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
            status = reservation.getStatus().name
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
            status = ReservationStatus.valueOf(entity.status)
        )
    }
}
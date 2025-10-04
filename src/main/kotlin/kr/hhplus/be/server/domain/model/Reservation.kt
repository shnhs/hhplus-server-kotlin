package kr.hhplus.be.server.domain.model

import kr.hhplus.be.server.interfaces.dto.ReservationDto.ReservationResponseDto
import java.time.LocalDateTime
import java.util.*

enum class ReservationStatus {
    PENDING,
    EXPIRED,
    CONFIRMED
}

class Reservation(
    private val uuid: String,
    private val concertId: String,
    private val scheduleId: String,
    private val seatNumber: Int,
    private val userId: String,
    private val reservedAt: LocalDateTime
) {
    private var status: ReservationStatus = ReservationStatus.PENDING

    companion object {
        fun create(
            concertId: String,
            scheduleId: String,
            seatNumber: Int,
            userId: String
        ): Reservation {
            return Reservation(
                uuid = UUID.randomUUID().toString(),
                concertId = concertId,
                scheduleId = scheduleId,
                seatNumber = seatNumber,
                userId = userId,
                reservedAt = LocalDateTime.now()
            )
        }

        // Infrastructure에서 DB 데이터로 도메인 객체 재구성할 때 사용
        fun createWithUuid(
            uuid: String,
            concertId: String,
            scheduleId: String,
            seatNumber: Int,
            userId: String,
            reservedAt: LocalDateTime,
            status: ReservationStatus
        ): Reservation {
            return Reservation(
                uuid = uuid,
                concertId = concertId,
                scheduleId = scheduleId,
                seatNumber = seatNumber,
                userId = userId,
                reservedAt = reservedAt,
            ).apply {
                this.status = status
            }
        }
    }

    // 만료 확인
    fun isExpired(): Boolean {
        return reservedAt.plusMinutes(5).isBefore(LocalDateTime.now())
    }

    // 예약 확정 (결제 완료 시)
    fun confirm() {
        if (isExpired()) {
            throw IllegalStateException("만료된 예약은 확정할 수 없습니다.")
        }
        if (this.status == ReservationStatus.CONFIRMED) {
            throw IllegalStateException("이미 확정된 예약입니다.")
        }
        this.status = ReservationStatus.CONFIRMED
    }

    fun toDto(): ReservationResponseDto {
        return ReservationResponseDto(
            this.uuid,
            this.concertId,
            this.scheduleId,
            this.seatNumber,
            this.status.name,
            this.reservedAt
        )
    }
    
    // Getters
    fun getUuid(): String = uuid
    fun getConcertId(): String = concertId
    fun getScheduleId(): String = scheduleId
    fun getSeatNumber(): Int = seatNumber
    fun getUserId(): String = userId
    fun getStatus(): ReservationStatus = status
    fun getReservedAt(): LocalDateTime = reservedAt
}
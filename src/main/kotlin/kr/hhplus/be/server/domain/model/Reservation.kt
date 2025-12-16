package kr.hhplus.be.server.domain.model

import kr.hhplus.be.server.enums.ReservationStatus
import kr.hhplus.be.server.interfaces.dto.ReservationDto.ReservationResponseDto
import java.time.LocalDateTime
import java.util.*


class Reservation(
    private val uuid: String,
    private val scheduleId: String,
    private val seatId: String,
    private var userId: String,
    private var reservedAt: LocalDateTime,
    private var status: ReservationStatus
) {

    companion object {
        fun create( // 콘서트 일정 생성용
            scheduleId: String,
            seatId: String,
            userId: String
        ): Reservation {
            return Reservation(
                uuid = UUID.randomUUID().toString(),
                scheduleId = scheduleId,
                seatId = seatId,
                userId = userId,
                reservedAt = LocalDateTime.now(),
                status = ReservationStatus.PENDING // 좌석 임시점유
            )
        }

        // Infrastructure에서 DB 데이터로 도메인 객체 재구성할 때 사용
        fun createWithUuid(
            uuid: String,
            scheduleId: String,
            seatId: String,
            userId: String,
            reservedAt: LocalDateTime,
            status: ReservationStatus
        ): Reservation {
            return Reservation(
                uuid = uuid,
                scheduleId = scheduleId,
                seatId = seatId,
                userId = userId,
                reservedAt = reservedAt,
                status = status
            )
        }
    }

    // 임시점유 만료확인
    fun isExpired(): Boolean {
        return reservedAt.plusMinutes(5)!!.isBefore(LocalDateTime.now())
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
            this.scheduleId,
            this.seatId,
            this.status,
            this.userId,
            this.reservedAt
        )
    }

    // Getters
    fun getUuid(): String = uuid
    fun getScheduleId(): String = scheduleId
    fun getSeatId(): String = seatId
    fun getUserId(): String = userId
    fun getStatus(): ReservationStatus = status
    fun getReservedAt(): LocalDateTime = reservedAt
}
package kr.hhplus.be.server.interfaces.dto

import kr.hhplus.be.server.enums.ReservationStatus
import java.time.LocalDateTime

class ReservationDto {

    data class ReservationRequestDto(
        val seatId: String,
        val userId: String
    )

    data class ReservationResponseDto(
        val uuid: String,
        val scheduleId: String,
        val seatId: String,
        val status: ReservationStatus,
        val userId: String,
        val reservedAt: LocalDateTime
    )
}
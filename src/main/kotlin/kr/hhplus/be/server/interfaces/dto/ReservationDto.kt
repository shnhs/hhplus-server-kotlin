package kr.hhplus.be.server.interfaces.dto

import java.time.LocalDateTime

class ReservationDto {

    data class ReservationRequestDto(
        val concertId: String,
        val scheduleId: String,
        val seatNumber: Int,
        val userId: String
    )

    data class ReservationResponseDto(
        val uuid: String,
        val concertId: String,
        val scheduleId: String,
        val seatNumber: Int,
        val status: String,
        val userId: String?,
        val reservedAt: LocalDateTime?
    )
}
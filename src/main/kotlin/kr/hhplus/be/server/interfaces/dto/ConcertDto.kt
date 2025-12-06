package kr.hhplus.be.server.interfaces.dto

import kr.hhplus.be.server.enums.ReservationStatus
import java.time.LocalDateTime

class ConcertDto {

    data class ConcertResponseDto(
        val concertId: String,
        val concertName: String,
        val artist: String,
        val concertHall: String
    )

    data class ConcertScheduleResponseDto(
        val scheduleId: String,
        val concertId: String,
        val concertDate: LocalDateTime
    )

    data class ConcertSeatResponseDto(
        val seatId: String,
        val concertId: String,
        val scheduleId: String,
        val seatNumber: Int,
        val status: ReservationStatus
    )
}
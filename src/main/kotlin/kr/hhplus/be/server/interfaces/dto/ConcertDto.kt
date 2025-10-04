package kr.hhplus.be.server.interfaces.dto

import java.time.LocalDateTime

class ConcertDto {

    data class ConcertResponseDto(
        val concertId: String,
        val concertName: String,
        val artist: String,
        val concertHall: String,
        val availableSchedules: List<LocalDateTime>
    )
}
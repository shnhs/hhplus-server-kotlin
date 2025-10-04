package kr.hhplus.be.server.interfaces.dto

import java.time.LocalDateTime

class ConcertScheduleDto {

    data class ConcertScheduleResponseDto(
        val scheduleId: String,
        val concertId: String,
        val concertDate: LocalDateTime,
        val availableSeats: List<Int>
    )
}
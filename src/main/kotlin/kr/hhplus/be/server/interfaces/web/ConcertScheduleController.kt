package kr.hhplus.be.server.interfaces.web

import kr.hhplus.be.server.application.ConcertScheduleService
import kr.hhplus.be.server.interfaces.dto.CommonResponseDto
import kr.hhplus.be.server.interfaces.dto.ConcertScheduleDto.ConcertScheduleResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedule")
class ConcertScheduleController(
    private val concertScheduleService: ConcertScheduleService
) {

    /**
     * 특정 스케줄의 예약가능한 좌석 목록 조회
     */
    @GetMapping("/{scheduleId}")
    fun getAvailableSeats(@PathVariable scheduleId: String):
            CommonResponseDto<ConcertScheduleResponseDto> {
        val availableSeats: ConcertScheduleResponseDto =
            concertScheduleService.getAvailableSeats(
                scheduleId = scheduleId
            )

        return CommonResponseDto(availableSeats)
    }

}
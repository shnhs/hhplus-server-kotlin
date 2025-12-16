package kr.hhplus.be.server.interfaces.web

import io.swagger.v3.oas.annotations.Operation
import kr.hhplus.be.server.application.ConcertScheduleService
import kr.hhplus.be.server.application.ConcertSeatService
import kr.hhplus.be.server.interfaces.dto.CommonResponseDto
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertSeatResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/schedule")
class ConcertScheduleController(
    private val concertScheduleService: ConcertScheduleService,
    private val concertSeatService: ConcertSeatService
) {

    @Operation(
        summary = "특정 콘서트 스케줄의 좌석 목록 조회",
        description = ""
    )
    @GetMapping("/{scheduleId}/seats")
    fun getConcertScheduleSeats(@PathVariable scheduleId: String)
            : CommonResponseDto<List<ConcertSeatResponseDto>> {
        return CommonResponseDto(
            concertSeatService.getScheduleSeats(
                concertScheduleId = scheduleId
            )
        )
    }
}
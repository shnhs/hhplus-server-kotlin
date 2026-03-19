package kr.hhplus.be.server.interfaces.web

import io.swagger.v3.oas.annotations.Operation
import kr.hhplus.be.server.application.ConcertScheduleService
import kr.hhplus.be.server.application.ConcertService
import kr.hhplus.be.server.interfaces.dto.CommonResponseDto
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertResponseDto
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertScheduleResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/concert")
class ConcertController(
    private val concertService: ConcertService,
    private val concertScheduleService: ConcertScheduleService
) {

    @Operation(
        summary = "특정 콘서트 상세 조회",
        description = ""
    )
    @GetMapping("/{concertId}")
    fun getConcertDetail(@PathVariable concertId: String)
            : CommonResponseDto<ConcertResponseDto> {
        return CommonResponseDto(
            concertService.getConcertDetail(
                concertId = concertId
            )
        )
    }

    @Operation(
        summary = "특정 콘서트 스케줄 목록 조회",
        description = ""
    )
    @GetMapping("/{concertId}/schedules")
    fun getConcertScheduleDetail(@PathVariable concertId: String)
            : CommonResponseDto<List<ConcertScheduleResponseDto>> {
        return CommonResponseDto(
            concertScheduleService.getSchedules(
                concertId = concertId
            )
        )
    }

    @GetMapping("/imminent")
    fun getImminentConcerts()
            : CommonResponseDto<List<ConcertResponseDto>> {
        return CommonResponseDto(
            concertService.getImminentConcerts()
        )
    }
}
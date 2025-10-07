package kr.hhplus.be.server.interfaces.web

import kr.hhplus.be.server.application.ConcertService
import kr.hhplus.be.server.interfaces.dto.CommonResponseDto
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertResponseDto
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/api/concert")
class ConcertController(
    private val concertService: ConcertService
) {

    /**
     * 특정 콘서트의 상세정보 조회(스케줄 목록 조회)
     */
    @GetMapping("/{concertId}")
    fun getConcertSchedules(@PathVariable concertId: String)
            : CommonResponseDto<ConcertResponseDto> {
        return CommonResponseDto(
            concertService.getAvailableConcertSchedules(
                concertId = concertId
            )
        )
    }
}
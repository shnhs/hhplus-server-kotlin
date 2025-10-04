package kr.hhplus.be.server.interfaces.web

import kr.hhplus.be.server.application.PointService
import kr.hhplus.be.server.domain.model.User
import kr.hhplus.be.server.interfaces.dto.CommonResponseDto
import kr.hhplus.be.server.interfaces.dto.PointDto
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/point")
class PointController(
    private val pointService: PointService
) {

    /**
     * 사용자 포인트 충전
     */
    @PostMapping("/charge")
    fun chargePoint(
        @RequestBody request: PointDto.PointChartRequestDto
    ): CommonResponseDto<User> {
        val userPointCharged = pointService.chargePoint(
            request.userId,
            request.amount
        )

        return CommonResponseDto(userPointCharged)
    }

}
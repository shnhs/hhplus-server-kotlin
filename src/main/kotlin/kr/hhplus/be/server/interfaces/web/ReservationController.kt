package kr.hhplus.be.server.interfaces.web

import kr.hhplus.be.server.application.ReservationService
import kr.hhplus.be.server.interfaces.dto.CommonResponseDto
import kr.hhplus.be.server.interfaces.dto.ReservationDto.ReservationRequestDto
import kr.hhplus.be.server.interfaces.dto.ReservationDto.ReservationResponseDto
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping(name = "/api/reservations")
class ReservationController(
    private val reservationService: ReservationService
) {

    @PostMapping
    fun postReserve(@RequestBody request: ReservationRequestDto)
            : CommonResponseDto<ReservationResponseDto> {
        val reservation =
            reservationService.reserve(
                seatId = request.seatId,
                userId = request.userId
            )
        return CommonResponseDto<ReservationResponseDto>(reservation.toDto())
    }

    @PostMapping("/{reservationId}/confirm")
    fun postConfirmReservation(@PathVariable reservationId: String)
            : CommonResponseDto<ReservationResponseDto> {
        val reservation = reservationService.confirmReservation(
            reservationId = reservationId
        )
        return CommonResponseDto<ReservationResponseDto>(reservation.toDto())
    }
}
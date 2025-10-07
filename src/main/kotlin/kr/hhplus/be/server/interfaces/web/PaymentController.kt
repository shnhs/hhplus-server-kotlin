package kr.hhplus.be.server.interfaces.web

import kr.hhplus.be.server.application.PaymentService
import kr.hhplus.be.server.interfaces.dto.CommonResponseDto
import kr.hhplus.be.server.interfaces.dto.PaymentDto.PaymentResponseDto
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/payment")
class PaymentController(
    private val paymentService: PaymentService
) {

    /**
     * 예약결제
     */
    @PostMapping("/{reservationId)")
    fun postPayment(@PathVariable reservationId: String)
            : CommonResponseDto<PaymentResponseDto> {
        val payment = paymentService.postPayment(reservationId = reservationId)

        return CommonResponseDto(payment.toDto())
    }

}
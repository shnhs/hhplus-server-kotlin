package kr.hhplus.be.server.interfaces.dto

import java.time.LocalDateTime

class PaymentDto {

    data class PaymentResponseDto(
        val paymentId: String,
        val reservationId: String,
        val userId: String,
        val createdAt: LocalDateTime
    )
}
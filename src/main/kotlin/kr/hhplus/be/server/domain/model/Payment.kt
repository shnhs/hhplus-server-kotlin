package kr.hhplus.be.server.domain.model

import kr.hhplus.be.server.interfaces.dto.PaymentDto.PaymentResponseDto
import java.time.LocalDateTime
import java.util.*

class Payment(
    private val paymentId: String,
    private val reservationId: String,
    private val userId: String,
    private val createdAt: LocalDateTime
) {
    companion object {
        fun create(
            reservationId: String,
            userId: String
        ): Payment {
            return Payment(
                paymentId = UUID.randomUUID().toString(),
                reservationId = reservationId,
                userId = userId,
                createdAt = LocalDateTime.now()
            )
        }

        fun createWithUuid(
            paymentId: String,
            reservationId: String,
            userId: String,
            createdAt: LocalDateTime
        ): Payment {
            return Payment(
                paymentId = paymentId,
                reservationId = reservationId,
                userId = userId,
                createdAt = createdAt
            )
        }
    }

    // getters
    fun getPaymentId(): String = paymentId
    fun getReservationId(): String = reservationId
    fun getUserId(): String = userId
    fun getCreatedAt(): LocalDateTime = createdAt

    fun toDto(): PaymentResponseDto {
        return PaymentResponseDto(
            paymentId = this.paymentId,
            reservationId = this.reservationId,
            userId = this.userId,
            createdAt = this.createdAt
        )
    }
}
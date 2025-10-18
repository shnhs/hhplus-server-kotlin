package kr.hhplus.be.server.domain.model

import java.time.LocalDateTime
import java.util.*


data class ReservationToken(
    val tokenId: String,
    val userId: String,

    ) {
    var createdAt: LocalDateTime = LocalDateTime.now()

    companion object {
        fun create(
            userId: String
        ): ReservationToken {
            return ReservationToken(
                tokenId = UUID.randomUUID().toString(),
                userId = userId,
            )
        }

        fun createWithUuid(
            tokenId: String,
            userId: String,
            createdAt: LocalDateTime
        ): ReservationToken {
            return ReservationToken(
                tokenId = tokenId,
                userId = userId,
            ).apply {
                this.createdAt = createdAt
            }
        }
    }

    // 토큰 만료 확인
    fun isExpired(): Boolean {
        return createdAt.plusMinutes(5).isBefore(
            LocalDateTime.now()
        )
    }
}
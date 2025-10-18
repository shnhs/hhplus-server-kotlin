package kr.hhplus.be.server.domain.model

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import java.time.LocalDateTime

class ReservationTokenTest : BehaviorSpec({
    given("유효한 사용자가 있을 경우") {
        val testUserId = "USER_ID"

        `when`("대기열토큰 발급을 위해 create 메서드를 호출한다.") {
            val reservationToken = ReservationToken.create(
                userId = testUserId
            )
            then("사용자에게 올바르게 귀속된다") {
                reservationToken.userId shouldBe testUserId
            }
        }

        `when`("토큰이 발급된지 5분 이전일 경우") {
            val activeToken = ReservationToken.createWithUuid(
                tokenId = "TOKEN_ID",
                userId = testUserId,
                createdAt = LocalDateTime.now().minusMinutes(4)
            )
            then("만료로 판단하지 않는다.") {
                activeToken.isExpired() shouldBe false
            }
        }

        `when`("토큰이 발급된지 5분 이상 지났을 경우") {
            val expiredToken = ReservationToken.createWithUuid(
                tokenId = "TOKEN_ID",
                userId = testUserId,
                createdAt = LocalDateTime.now().minusMinutes(6)
            )
            then("만료되었다고 판단한다.") {
                expiredToken.isExpired() shouldBe true
            }
        }
    }

    given("토큰이 있을 때") {
        val token = ReservationToken.createWithUuid(
            tokenId = "TOKEN_ID",
            userId = "USER_ID",
            createdAt = LocalDateTime.now().minusMinutes(4)
        )
        `when`("새로운 만료시간을 지정하면") {
            val newCreatedAt = LocalDateTime.now()
            token.createdAt = newCreatedAt
            then("정상적으로 반영된다.") {
                token.createdAt shouldBe newCreatedAt
            }
        }
    }
})
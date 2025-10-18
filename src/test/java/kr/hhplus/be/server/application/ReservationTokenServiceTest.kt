package kr.hhplus.be.server.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.model.ReservationToken
import kr.hhplus.be.server.domain.repo.ReservationTokenRepo
import java.time.LocalDateTime

class ReservationTokenServiceTest : BehaviorSpec({
    val reservationTokenRepo = mockk<ReservationTokenRepo>()
    val reservationTokenService = ReservationTokenService(reservationTokenRepo)

    val testUserId = "TEST_USER_ID"

    given("처음 요청하는 사용자가 있을 때") {
        every {
            reservationTokenRepo.findByUserId(testUserId)
        } returns null
        every { // save 메서드 모킹
            reservationTokenRepo.save(any())
        } answers { firstArg() }

        `when`("대기열 토큰 발급요청을 할 경우") {
            val reservationToken: ReservationToken =
                reservationTokenService.postReservationToken(
                    userId = testUserId
                )
            then("사용자에게 귀속된 대기열 토큰이 정상적으로 생성된다.") {
                reservationToken.userId shouldBe testUserId
            }
            then("save 메서드가 정상적으로 호출된다.") {
                verify { reservationTokenRepo.save(any()) }
            }
        }
    }

    given("사용자가 이미 유효한 대기열 토큰이 있을때") {
        every {
            reservationTokenRepo.findByUserId(testUserId)
        } returns ReservationToken.createWithUuid(
            tokenId = "TOKEN_ID",
            userId = testUserId,
            createdAt = LocalDateTime.now().minusMinutes(2)
        )
        `when`("사용자가 대기열 토큰을 요청하면") {
            val reservationToken = reservationTokenService.postReservationToken(
                testUserId
            )
            then("그대로 반환된다.") {
                reservationToken.userId shouldBe testUserId
                reservationToken.tokenId shouldBe "TOKEN_ID"
            }
        }
    }

    given("사용자가 만료된 대기열 토큰이 있을때") {
        val expiredToken = ReservationToken.createWithUuid(
            tokenId = "TOKEN_ID",
            userId = testUserId,
            createdAt = LocalDateTime.now().minusMinutes(9)
        )

        every {
            reservationTokenRepo.findByUserId(testUserId)
        } returns expiredToken

        every {
            reservationTokenRepo.save(any())
        } answers { firstArg() }

        every {
            reservationTokenRepo.delete(expiredToken)
        } answers { expiredToken }

        `when`("사용자가 대기열 토큰을 요청하면") {
            val reservationToken = reservationTokenService.postReservationToken(
                testUserId
            )

            then("기존 만료된 토큰은 삭제된다.") {
                verify { reservationTokenRepo.delete(expiredToken) }
            }

            then("save 메서드가 정상적으로 호출된다.") {
                verify { reservationTokenRepo.save(reservationToken) }
            }
        }
    }
})
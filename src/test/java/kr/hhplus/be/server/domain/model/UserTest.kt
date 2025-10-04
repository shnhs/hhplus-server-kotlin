package kr.hhplus.be.server.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe

class UserTest : BehaviorSpec({
    given("유효한 사용자가 있을 경우") {
        val user = User.createWithUuid(
            userId = "USER_ID",
            userName = "USER_NAME",
            email = "USER_EMAIL",
            point = 100000
        )
        `when`("유효한 포인트를 사용하려고 하면") {
            user.usePoint(35000)
            then("포인트가 정상적으로 차감된다.") {
                user.getPoint() shouldBe 100000 - 35000
            }
        }

        `when`("보유 포인트보다 더 많은 포인트를 사용하려고 하면") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    user.usePoint(200000)
                }
            }
        }
        `when`("음수 포인트를 사용하려고 시도하면") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    user.usePoint(-120000)
                }
            }
        }
        `when`("음수 포인트를 충전하려고 시도하면") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    user.chargePoint(-120000)
                }
            }
        }
    }
})
package kr.hhplus.be.server.application

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.domain.model.User
import kr.hhplus.be.server.domain.repo.UserRepo

class PointServiceTest : BehaviorSpec({
    val userRepo = mockk<UserRepo>()
    val pointService = PointService(userRepo)

    given("유효한 사용자가 있을 경우") {
        every {
            userRepo.findByUuid("USER_ID")
        } returns User.createWithUuid(
            userId = "USER_ID",
            userName = "USER_NAME",
            email = "EMAIL",
            point = 15000,
        )

        every {
            userRepo.save(any())
        } answers { firstArg() }
        
        `when`("유효한 포인트를 충전할경우") {
            val userCharged = pointService.chargePoint(
                userId = "USER_ID", amount = 10000
            )
            then("정상적으로 포인트가 충전된다.") {
                userCharged.getPoint() shouldBe 15000 + 10000
            }
        }
    }
})
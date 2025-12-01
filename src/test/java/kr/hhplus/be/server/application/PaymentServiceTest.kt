package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.model.User
import kr.hhplus.be.server.domain.repo.PaymentRepo
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.domain.repo.UserRepo
import kr.hhplus.be.server.enums.ReservationStatus
import java.time.LocalDateTime

class PaymentServiceTest : BehaviorSpec({
    val paymentRepo = mockk<PaymentRepo>()
    val reservationRepo = mockk<ReservationRepo>()
    val userRepo = mockk<UserRepo>()

    val paymentService = PaymentService(
        paymentRepo = paymentRepo,
        reservationRepo = reservationRepo,
        userRepo = userRepo
    )

    given("결제 대기중인 예약이 있을 때") {
        every { // 예약조회 모킹
            reservationRepo.findByUuid("RESERVATION_ID")
        } returns Reservation.createWithUuid(
            uuid = "RESERVATION_ID",
            concertId = "CONCERT_ID",
            scheduleId = "SCHEDULE_ID",
            seatNumber = 18,
            userId = "USER_ID",
            reservedAt = LocalDateTime.now().minusMinutes(3),
            status = ReservationStatus.PENDING
        )

        every { // 예약 저장 모킹
            paymentRepo.save(any())
        } answers { firstArg() }

        `when`("사용자가 충분한 포인트가 있을때 결제를 시도하면") {
            every { // 사용자 조회 모킹
                userRepo.findByUuid("USER_ID")
            } returns User.createWithUuid(
                userId = "USER_ID",
                userName = "USER_NAME",
                email = "EMAIL",
                point = 100000
            )
            val payment = paymentService.postPayment(
                reservationId = "RESERVATION_ID"
            )
            then("정상적으로 예약이 생성된다.") {
                payment.getReservationId() shouldBe "RESERVATION_ID"
            }
        }
        `when`("사용자가 충분한 포인트가 없다면") {
            every { // 사용자 조회 모킹
                userRepo.findByUuid("USER_ID")
            } returns User.createWithUuid(
                userId = "USER_ID",
                userName = "USER_NAME",
                email = "EMAIL",
                point = 30000
            )
            then("결제에 실패한다.") {
                shouldThrow<IllegalArgumentException> {
                    paymentService.postPayment(
                        reservationId = "RESERVATION_ID"
                    )
                }
            }
        }
    }
})
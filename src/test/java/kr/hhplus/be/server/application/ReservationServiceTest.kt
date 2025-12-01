package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.enums.ReservationStatus
import java.time.LocalDateTime

class ReservationServiceTest : BehaviorSpec({
    val reservationRepo = mockk<ReservationRepo>()
    val reservationService = ReservationService(reservationRepo)

    given("좌석이 아직 예약가능할 때") {
        every {
            reservationRepo.findByConcertIdAndScheduleIdAndSeatNumber(
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 9
            )
        } returns Reservation.createWithUuid(
            uuid = "UUID01",
            concertId = "CONCERT_ID",
            scheduleId = "SCHEDULE_ID",
            seatNumber = 9,
            userId = null,
            reservedAt = null,
            status = ReservationStatus.AVAILABLE
        )

        // Repo save 모킹
        every {
            reservationRepo.save(any())
        } answers { firstArg() }

        `when`("예약을 요청하면") {
            val result = reservationService.reserve(
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 9,
                userId = "USER_ID"
            )
            then("예약 성공") {
                result shouldNotBe null
                result.getStatus() shouldBe ReservationStatus.PENDING
            }
            then("repo의 save가 호출되야 한다.") {
                verify { reservationRepo.save(any()) }
            }
        }
    }
    given("이미 선택된 좌석이라면") {
        val pendingReservation = Reservation.createWithUuid(
            uuid = "UUID01",
            concertId = "CONCERT_ID",
            scheduleId = "SCHEDULE_ID",
            seatNumber = 9,
            userId = "USER_ALREADY",
            reservedAt = LocalDateTime.now().minusMinutes(1),
            status = ReservationStatus.PENDING
        )

        every {
            reservationRepo.findByConcertIdAndScheduleIdAndSeatNumber(
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 9
            )
        } returns pendingReservation

        `when`("예약을 시도하면") {
            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    reservationService.reserve(
                        concertId = "CONCERT_ID",
                        scheduleId = "SCHEDULE_ID",
                        seatNumber = 9,
                        userId = "USER_LATE"
                    )
                }
            }
        }
    }
    given("만료되지 않은 예약이 있을때") {
        val reservation = Reservation.createWithUuid(
            uuid = "RESERVATION_ID",
            concertId = "CONCERT_ID",
            scheduleId = "SCHEDULE_ID",
            seatNumber = 8,
            userId = "USER_ID",
            reservedAt = LocalDateTime.now().minusMinutes(2),
            status = ReservationStatus.PENDING
        )
        every { // repo 모킹
            reservationRepo.findByUuid("RESERVATION_ID")
        } returns reservation

        every { // Repo save 모킹
            reservationRepo.save(any())
        } answers { firstArg() }

        `when`("확정하려고 시도하면") {
            val confirmReservation = reservationService.confirmReservation(
                "RESERVATION_ID"
            )
            then("확정 되어야 한다.") {
                confirmReservation.getStatus() shouldBe ReservationStatus.CONFIRMED
            }
            then("repo의 save가 호출되야 한다.") {
                verify { reservationRepo.save(any()) }
            }
        }
    }
    given("존재하지 않는 UUID의 예약이라면") {

        every { // repo 모킹
            reservationRepo.findByUuid("INVALID_UUID")
        } returns null

        `when`("확정을 시도하면") {
            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    reservationService.confirmReservation(
                        "INVALID_UUID"
                    )
                }
            }
        }
    }
})

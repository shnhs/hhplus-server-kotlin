package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kr.hhplus.be.server.Fixtures
import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.enums.ReservationStatus
import kr.hhplus.be.server.infrastructure.persistence.ConcertSeatJpaRepo
import org.redisson.api.RLock
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

class ReservationProcessorTest : BehaviorSpec({
    val reservationRepo = mockk<ReservationRepo>()
    val concertSeatRepo = mockk<ConcertSeatJpaRepo>()
    val reservationProcessor = ReservationProcessor(
        concertSeatRepo, reservationRepo
    )
    val mockLock = mockk<RLock>(relaxed = true)

    val fixture = Fixtures()

    val testConcertId = "TEST_CONCERT_ID"
    val testConcertScheduleId = "TEST_CONCERT_SCHEDULE_ID"
    val testConcertSeatId = "TEST_CONCERT_SEAT_ID"
    val testSeatNumber = 10
    val testUserId = "TEST_USER"

    given("좌석이 아직 예약가능할 때") {
        val availableTestSeat = fixture.concertSeat(
            concertId = testConcertId,
            concertScheduleId = testConcertScheduleId,
            seatNumber = testSeatNumber,
            status = ReservationStatus.AVAILABLE
        )


        every {
            mockLock.tryLock(5, 10, TimeUnit.SECONDS)
        } returns true

        every {
            concertSeatRepo.findByUuid(availableTestSeat.uuid)
        } returns availableTestSeat

        // Repo save 모킹
        every {
            reservationRepo.save(any())
        } answers { firstArg() }

        `when`("예약을 요청하면") {
            val result = reservationProcessor.proceedReservation(
                seatId = availableTestSeat.uuid,
                userId = testUserId
            )
            then("예약 성공") {
                result shouldNotBe null
                result.getStatus() shouldBe ReservationStatus.PENDING
                result.getUserId() shouldBe testUserId
                availableTestSeat.status shouldNotBe ReservationStatus.AVAILABLE
            }
            then("repo의 save가 호출되야 한다.") {
                verify { reservationRepo.save(any()) }
            }
        }
    }

    given("이미 선택된 좌석이라면") {
        val pendingTestSeat = fixture.concertSeat(
            concertId = testConcertId,
            concertScheduleId = testConcertScheduleId,
            seatNumber = testSeatNumber,
            status = ReservationStatus.PENDING
        )

        every {
            concertSeatRepo.findByUuid(pendingTestSeat.uuid)
        } returns pendingTestSeat

        `when`("예약을 시도하면") {
            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    reservationProcessor.proceedReservation(
                        seatId = pendingTestSeat.uuid,
                        userId = testUserId
                    )
                }
            }
        }
    }

    given("만료되지 않은 예약이 있을때") {
        val testReservationId = "TEST_RESERVATION"
        val reservation = Reservation.createWithUuid(
            uuid = testReservationId,
            scheduleId = testConcertScheduleId,
            seatId = testConcertSeatId,
            userId = testUserId,
            reservedAt = LocalDateTime.now().minusMinutes(2),
            status = ReservationStatus.PENDING
        )

        every { // repo 모킹
            reservationRepo.findByUuid(testReservationId)
        } returns reservation

        every { // Repo save 모킹
            reservationRepo.save(any())
        } answers { firstArg() }

        `when`("확정하려고 시도하면") {
            val confirmReservation = reservationProcessor.processConfirm(
                reservationId = testReservationId
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
                    reservationProcessor.processConfirm(
                        "INVALID_UUID"
                    )
                }
            }
        }
    }
})

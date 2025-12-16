package kr.hhplus.be.server.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kr.hhplus.be.server.enums.ReservationStatus
import java.time.LocalDateTime

class ReservationTest : BehaviorSpec({

    val testScheduleId = "TEST_SCHEDULE"
    val testSeatId = "TEST_SEAT"
    val testUserId = "TEST_USER"

    given("정상적으로 생성된 예약이 있을 경우") {
        val reservation = Reservation.create(
            scheduleId = testScheduleId,
            seatId = testSeatId,
            userId = testUserId
        )
        `when`("예약 정보를 확인하면") {
            then("공연 및 좌석 정보와 예약자가 정상적으로 조회된다.") {
                reservation.getScheduleId() shouldBe testScheduleId
                reservation.getSeatId() shouldBe testSeatId
                reservation.getUserId() shouldBe testUserId
            }
            then("기본 상태는 PENDING 상태이다.") {
                reservation.getStatus() shouldBe ReservationStatus.PENDING

            }
        }
    }

    given("예약 임시점유 후") {
        `when`("4분까지 지났을 땐") {
            val reservation = Reservation.createWithUuid(
                uuid = "UUID",
                scheduleId = "SCHEDULE_ID",
                seatId = "SEAT_ID",
                userId = "USER_ID",
                reservedAt = LocalDateTime.now().plusMinutes(4),
                status = ReservationStatus.PENDING
            )
            then("만료되지 않아야 한다.") {
                reservation.isExpired() shouldBe false
            }
        }
        `when`("임시 점유 후 5분이 넘어갔을 경우") {
            val reservation = Reservation.createWithUuid(
                uuid = "UUID",
                scheduleId = "SCHEDULE_ID",
                seatId = "SEAT_ID",
                userId = "USER_ID",
                reservedAt = LocalDateTime.now().minusMinutes(6),
                status = ReservationStatus.PENDING
            )
            then("만료되어야 한다.") {
                reservation.isExpired() shouldBe true
            }
        }
    }

    given("만료되지 않은 예약은") {
        val reservation = Reservation.createWithUuid(
            uuid = "UUID",
            scheduleId = "SCHEDULE_ID",
            seatId = "SEAT_ID",
            userId = "USER_ID",
            reservedAt = LocalDateTime.now().minusMinutes(2),
            status = ReservationStatus.PENDING
        )
        `when`("확정을 시도하면") {
            reservation.confirm()
            then("CONFIRMED로 상태가 바뀌어야 한다.") {
                reservation.getStatus() shouldBe ReservationStatus.CONFIRMED
            }
        }
    }
    given("만료된 예약은") {
        val reservation = Reservation.Companion.createWithUuid(
            uuid = "UUID",
            scheduleId = "SCHEDULE_ID",
            seatId = "SEAT_ID",
            userId = "USER_ID",
            reservedAt = LocalDateTime.now().minusMinutes(6),
            status = ReservationStatus.PENDING
        )
        `when`("확정을 시도하면") {
            then("예외가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    reservation.confirm()
                }
            }
        }

    }
})
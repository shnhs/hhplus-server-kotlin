package kr.hhplus.be.server.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldBeUUID
import java.time.LocalDateTime

class ReservationTest : BehaviorSpec({
    given("새로운 예약 생성 시") {
        `when`("create 메서드가 호출된다") {
            val reservation = Reservation.create(
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 8,
                userId = "USER_ID"
            )

            then("기본 상태는 PENDING 이다.") {
                reservation.getStatus() shouldBe ReservationStatus.PENDING
            }

            then("필드가 정상적으로 설정된다.") {
                reservation.getUuid().shouldBeUUID()
                reservation.getConcertId() shouldBe "CONCERT_ID"
                reservation.getScheduleId() shouldBe "SCHEDULE_ID"
                reservation.getSeatNumber() shouldBe 8
                reservation.getUserId() shouldBe "USER_ID"
            }
        }
    }

    given("예약 생성 후") {
        `when`("4분까지 지났을 땐") {
            val reservation = Reservation.Companion.createWithUuid(
                uuid = "UUID",
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 8,
                userId = "USER_ID",
                reservedAt = LocalDateTime.now().plusMinutes(4),
                status = ReservationStatus.PENDING
            )
            then("만료되지 않아야 한다.") {
                reservation.isExpired() shouldBe false
            }
        }
        `when`("5분이 넘어갔을 경우") {
            val reservation = Reservation.Companion.createWithUuid(
                uuid = "UUID",
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 8,
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
        val reservation = Reservation.Companion.createWithUuid(
            uuid = "UUID",
            concertId = "CONCERT_ID",
            scheduleId = "SCHEDULE_ID",
            seatNumber = 8,
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
            concertId = "CONCERT_ID",
            scheduleId = "SCHEDULE_ID",
            seatNumber = 8,
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
package kr.hhplus.be.server.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import kr.hhplus.be.server.enums.ReservationStatus
import java.time.LocalDateTime

class ReservationTest : BehaviorSpec({
    given("예약 시도 시") {
        `when`("예약 가능한 항목은") {
            val reservation = Reservation.create(
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 8
            )

            then("예약자명이 비어있다.") {
                reservation.getUserId() shouldBe null
            }

            then("예약가능 상태로 조회된다.") {
                reservation.isAvailable() shouldBe true
            }
        }
    }

    given("예약가능한 일정이 있을 때") {
        val reservation = Reservation.create(
            concertId = "CONCERT_ID",
            scheduleId = "SCHEDULE_ID",
            seatNumber = 8
        )
        `when`("예약을 시도하면") {
            val reservationUser: String = "USER_01"
            reservation.reserve(reservationUser)
            then("사용자에게 임시점유되고 PENDING 상태가 된다.") {
                reservation.getUserId() shouldBe reservationUser
                reservation.getStatus() shouldBe ReservationStatus.PENDING
            }
        }
    }

    given("예약 임시점유 후") {
        `when`("4분까지 지났을 땐") {
            val reservation = Reservation.createWithUuid(
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
        `when`("임시 점유 후 5분이 넘어갔을 경우") {
            val reservation = Reservation.createWithUuid(
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
        val reservation = Reservation.createWithUuid(
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
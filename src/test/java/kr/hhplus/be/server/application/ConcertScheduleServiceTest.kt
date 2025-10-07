package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldNotContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.model.ReservationStatus
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.entity.ConcertScheduleEntity
import kr.hhplus.be.server.infrastructure.persistence.ConcertScheduleJpaRepo

class ConcertScheduleServiceTest : BehaviorSpec({
    val concertScheduleJpaRepo = mockk<ConcertScheduleJpaRepo>()
    val reservationRepo = mockk<ReservationRepo>()
    val concertScheduleService = ConcertScheduleService(
        concertScheduleJpaRepo, reservationRepo
    )

    given("유효한 스케줄 아이디가 있을 경우") {
        every {
            reservationRepo.findByScheduleIdAndStatusIn(
                scheduleId = "SCHEDULE_ID",
                status = listOf(
                    ReservationStatus.CONFIRMED, ReservationStatus.PENDING
                )
            )
        } returns listOf(
            Reservation.create(
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 9,
                userId = "USER_ID01"
            ), Reservation.create(
                concertId = "CONCERT_ID",
                scheduleId = "SCHEDULE_ID",
                seatNumber = 19,
                userId = "USER_ID02"
            )
        )

        every {
            concertScheduleJpaRepo.findByUuid("SCHEDULE_ID")
        } returns ConcertScheduleEntity()

        `when`("예약가능한 좌석을 조회할 경우") {
            val scheduleResponseDto = concertScheduleService.getAvailableSeats(
                scheduleId = "SCHEDULE_ID"
            )

            then("정상적으로 예약가능한 좌석번호들이 조회된다.") {
                scheduleResponseDto.availableSeats.size shouldBe 48
                scheduleResponseDto.availableSeats shouldNotContain 9
                scheduleResponseDto.availableSeats shouldNotContain 19
            }
        }
    }

    given("유효하지 않는 스케줄 아이디가 입력되었을때") {
        every {
            concertScheduleJpaRepo.findByUuid("INVALID_ID")
        } returns null

        `when`("예약가능 좌석을 조회하면") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    concertScheduleService.getAvailableSeats("INVALID_ID")
                }
            }
        }
    }
})
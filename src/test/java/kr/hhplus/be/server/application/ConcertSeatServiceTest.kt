package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.Fixtures
import kr.hhplus.be.server.infrastructure.persistence.ConcertSeatJpaRepo

class ConcertSeatServiceTest : BehaviorSpec({
    val concertSeatRepo = mockk<ConcertSeatJpaRepo>()
    val concertSeatService = ConcertSeatService(
        concertSeatRepo
    )

    val testConcertId = "TEST_CONCERT_ID"
    val testConcertScheduleId = "TEST_CONCERT_SCHEDULE_ID"
    val maxSeatNumber = 10

    val testConcertSeats = Fixtures().concertSeats(
        concertId = testConcertId,
        concertScheduleId = testConcertScheduleId,
        maxSeatNumber = maxSeatNumber
    )

    given("유효한 콘서트 일정 아이디가 있을 경우") {
        every {
            concertSeatRepo.findByConcertScheduleId(
                concertScheduleId = testConcertScheduleId
            )
        } returns testConcertSeats

        `when`("좌석 목록을 조회하면") {
            val seats = concertSeatService.getScheduleSeats(
                concertScheduleId = testConcertScheduleId
            )
            then("정상적으로 좌석 목록이 조회된다.") {
                seats.size shouldBe maxSeatNumber
                seats.first().scheduleId shouldBe testConcertScheduleId
                seats.first().concertId shouldBe testConcertId
            }
        }
    }

    given("유효하지 않은 콘서트일정 아이디가 있을 경우") {
        val invalidScheduleId = "INVALID_SCHEDULE_ID"

        every {
            concertSeatRepo.findByConcertScheduleId(
                concertScheduleId = invalidScheduleId
            )
        } returns emptyList()

        `when`("좌석 목록을 조회하면") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    concertSeatService.getScheduleSeats(
                        concertScheduleId = invalidScheduleId
                    )
                }
            }
        }
    }

})
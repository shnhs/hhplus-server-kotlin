package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.Fixtures
import kr.hhplus.be.server.infrastructure.persistence.ConcertScheduleJpaRepo
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertScheduleResponseDto
import java.time.LocalDateTime

class ConcertScheduleServiceTest : BehaviorSpec({
    val concertScheduleJpaRepo = mockk<ConcertScheduleJpaRepo>()
    val concertScheduleService = ConcertScheduleService(
        concertScheduleJpaRepo
    )

    val fixture = Fixtures()

    given("유효한 콘서트 스케줄 아이디가 있을 경우") {
        val testConcertId = "CONCERT_ID"
        val concertDays = 3
        val maxSeatNumber = 10

        val testConcertSchedules = fixture.concertSchedule(
            concertId = testConcertId,
            startDate = LocalDateTime.of(
                2025, 12, 23,
                18, 30
            ),
            concertDays = concertDays,
            maxSeatNumber = maxSeatNumber
        )

        every {
            concertScheduleJpaRepo.findByConcertId(
                concertId = testConcertId
            )
        } returns testConcertSchedules

        `when`("해당 콘서트 일정의 정보를 조회할 경우") {
            val schedules: List<ConcertScheduleResponseDto> =
                concertScheduleService.getSchedules(testConcertId)
            then("정상적으로 조회된다.") {
                schedules.size shouldBe concertDays * maxSeatNumber
                schedules.first().concertId shouldBe testConcertId
            }
        }
    }

    given("유효하지 않은 콘서트 아이디가 있다면") {
        val invalidConcertId = "INVALID_CONCERT_ID"

        every {
            concertScheduleJpaRepo.findByConcertId(
                concertId = invalidConcertId
            )
        } returns emptyList()

        `when`("콘서트 일정 정보를 조회할 경우") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalArgumentException> {
                    concertScheduleService.getSchedules(invalidConcertId)
                }
            }
        }
    }
})
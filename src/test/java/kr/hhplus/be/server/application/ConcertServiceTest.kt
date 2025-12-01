package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.Fixtures
import kr.hhplus.be.server.infrastructure.persistence.ConcertJpaRepo
import kr.hhplus.be.server.infrastructure.persistence.ConcertScheduleJpaRepo
import java.time.LocalDateTime

class ConcertServiceTest : BehaviorSpec({
    val concertJpaRepo = mockk<ConcertJpaRepo>()
    val scheduleJpaRepo = mockk<ConcertScheduleJpaRepo>()
    val concertService = ConcertService(
        concertJpaRepo = concertJpaRepo,
        scheduleJpaRepo = scheduleJpaRepo
    )
    val fixture = Fixtures()

    given("유효한 콘서트 아이디가 주어질때") {

        val testConcert = fixture.concert(
            concertName = "연말음악회",
            concertHall = "롯데콘서트홀",
            artist = "서울시향"
        )
        val testConcertId = testConcert.uuid

        val testConcertSchedules = fixture.concertSchedule(
            concertId = testConcertId,
            startDate = LocalDateTime.of(
                2025, 12, 23,
                18, 30
            ),
            performanceCount = 3,
            seatCount = 10
        )

        every {
            concertJpaRepo.findByUuid(testConcertId)
        } returns testConcert

        every {
            scheduleJpaRepo.findByConcertId(testConcertId)
        } returns testConcertSchedules


        `when`("콘서트 정보를 조회하면") {
            val concertSchedules = concertService.getAvailableConcertSchedules(
                testConcertId
            )

            then("콘서트 정보가 반환된다.") {
                concertSchedules.concertName shouldBe "연말음악회"
                concertSchedules.availableSchedules.size shouldBe testConcertSchedules.size
            }
            then("콘서트 스케줄 정보가 맞게 반환된다.") {
                concertSchedules.availableSchedules shouldContain
                        LocalDateTime.of(
                            2025, 12, 23,
                            18, 30
                        )

                concertSchedules.availableSchedules shouldContain
                        LocalDateTime.of(
                            2025, 12, 24,
                            18, 30
                        )
            }
        }
    }

    given("유효하지 않은 콘서트 아이디가 있을때") {
        every {
            concertJpaRepo.findByUuid("INVALID_ID")
        } returns null

        `when`("유효하지 않은 아이디로 정보를 조회하면") {
            then("에러가 발생한다.") {
                shouldThrow<IllegalStateException> {
                    concertService.getAvailableConcertSchedules("INVALID_ID")
                }
            }
        }
    }
})
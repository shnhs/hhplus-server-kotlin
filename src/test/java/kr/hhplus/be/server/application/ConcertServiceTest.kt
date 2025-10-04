package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.entity.ConcertEntity
import kr.hhplus.be.server.entity.ConcertScheduleEntity
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

    given("유효한 콘서트 아이디가 주어질때") {
        every {
            concertJpaRepo.findByUuid("CONCERT_ID")
        } returns ConcertEntity().apply {
            this.concertName = "연말음악회"
            this.artist = "서울시향"
            this.concertHall = "예술의 전당"
        }

        every {
            scheduleJpaRepo.findByConcertId("CONCERT_ID")
        } returns listOf(
            ConcertScheduleEntity().apply {
                this.id = 1
                this.uuid = "SCHEDULE_01"
                this.concertId = "CONCERT_ID"
                this.concertDate = LocalDateTime.of(
                    2025, 12, 23,
                    18, 30
                )
                this.availableSeats = 50
            },
            ConcertScheduleEntity().apply {
                this.id = 1
                this.uuid = "SCHEDULE_01"
                this.concertId = "CONCERT_ID"
                this.concertDate = LocalDateTime.of(
                    2025, 12, 24,
                    18, 30
                )
                this.availableSeats = 50
            }
        )

        `when`("아이디로 콘서트 정보를 조회하면") {
            val concertSchedules = concertService.getAvailableConcertSchedules(
                "CONCERT_ID"
            )

            then("콘서트 정보가 반환된다.") {
                concertSchedules.concertName shouldBe "연말음악회"
                concertSchedules.artist shouldBe "서울시향"
                concertSchedules.concertHall shouldBe "예술의 전당"
                concertSchedules.availableSchedules.size shouldBe 2
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
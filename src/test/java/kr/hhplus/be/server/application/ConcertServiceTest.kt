package kr.hhplus.be.server.application

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import kr.hhplus.be.server.Fixtures
import kr.hhplus.be.server.infrastructure.persistence.ConcertJpaRepo

class ConcertServiceTest : BehaviorSpec({
    val concertJpaRepo = mockk<ConcertJpaRepo>()
    val concertService = ConcertService(
        concertJpaRepo = concertJpaRepo
    )
    val fixture = Fixtures()

    given("유효한 콘서트 아이디가 주어질때") {

        val testConcert = fixture.concert(
            concertName = "연말음악회",
            concertHall = "롯데콘서트홀",
            artist = "서울시향"
        )
        val testConcertId = testConcert.uuid

        every {
            concertJpaRepo.findByUuid(testConcertId)
        } returns testConcert


        `when`("콘서트 정보를 조회하면") {
            val concert = concertService.getConcertDetail(
                testConcertId
            )

            then("콘서트 정보가 반환된다.") {
                concert.concertName shouldBe "연말음악회"
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
                    concertService.getConcertDetail("INVALID_ID")
                }
            }
        }
    }
})
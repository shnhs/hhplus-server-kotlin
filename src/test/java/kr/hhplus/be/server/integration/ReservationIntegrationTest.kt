package kr.hhplus.be.server.integration

import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kr.hhplus.be.server.TestcontainersConfiguration
import kr.hhplus.be.server.application.ReservationService
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.fixture.ReservationFixture
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger

@SpringBootTest
@Import(TestcontainersConfiguration::class)
class ReservationIntegrationTest(
    private val reservationService: ReservationService,
    private val reservationRepo: ReservationRepo,
    private val reservationFixture: ReservationFixture
) : BehaviorSpec({

    beforeSpec {
        reservationRepo.deleteAll() // 데이터 초기화
    }

    given("동일한 좌석에 대해") {
        val concertId = "CONCERT_1"
        val scheduleId = "SCHEDULE_1"
        val seatNumber = 1
        val userCount = 5 // 동시요청 수

        // Fixture로 초기화
        reservationFixture.createAvailableSeats(
            concertId = concertId, scheduleId = scheduleId, seatCount = 5
        )

        `when`("$userCount 명의 유저가 동시에 예약을 시도하면") {
            val executorService = Executors.newFixedThreadPool(userCount)
            val latch = CountDownLatch(userCount)
            val successCount = AtomicInteger(0)
            val failCount = AtomicInteger(0)

            val futures = (1..userCount).map { userId ->
                executorService.submit {
                    try {
                        latch.countDown()
                        latch.await() // 모든 스레드가 동시에 시작하도록 대기

                        reservationService.reserve(
                            concertId = concertId,
                            scheduleId = scheduleId,
                            seatNumber = seatNumber,
                            userId = "USER_$userId"
                        )
                        successCount.incrementAndGet()
                    } catch (e: Exception) {
                        failCount.incrementAndGet()
                    }
                }
            }
            // 모든 작업 완료 대기
            futures.forEach { it.get() }
            executorService.shutdown()
            executorService.awaitTermination(10, TimeUnit.SECONDS)

            then("1명만 예약에 성공한다") {
                successCount.get() shouldBe 1
            }

            then("나머지 ${userCount - 1}명은 예약에 실패한다") {
                failCount.get() shouldBe userCount - 1
            }

            then("DB에는 예약자가 기록된다") {
                val reservation = reservationRepo.findByConcertIdAndScheduleIdAndSeatNumberWithoutLock(
                    concertId = concertId,
                    scheduleId = scheduleId,
                    seatNumber = seatNumber
                )

                reservation shouldNotBe null
                reservation?.getUserId() shouldNotBe null
                reservation?.getReservedAt() shouldNotBe null
            }
        }
    }
})
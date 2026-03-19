package kr.hhplus.be.server.application

import kr.hhplus.be.server.enums.ReservationStatus
import kr.hhplus.be.server.infrastructure.persistence.ConcertJpaRepo
import kr.hhplus.be.server.infrastructure.persistence.ConcertScheduleJpaRepo
import kr.hhplus.be.server.infrastructure.persistence.ConcertSeatJpaRepo
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertResponseDto
import org.redisson.api.RedissonClient
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.stream.Collectors

@Service
class ConcertService(
    private val concertJpaRepo: ConcertJpaRepo,
    private val concertScheduleJpaRepo: ConcertScheduleJpaRepo,
    private val concertSeatJpaRepo: ConcertSeatJpaRepo,
    private val redissonClient: RedissonClient,
) {
    companion object {
        private const val IMMINENT_CONCERT_RANKING_KEY = "concert:imminent_ranking"
    }

    /**
     * 콘서트 정보 조회
     */
    fun getConcertDetail(concertId: String)
            : ConcertResponseDto {
        val concertEntity = concertJpaRepo.findByUuid(concertId)
            ?: throw IllegalStateException("존재하지 않는 콘서트 입니다.")

        return concertEntity.toDto()
    }

    /**
     * 매진 임박 콘서트 랭킹 조회
     */
    fun getImminentConcerts(): List<ConcertResponseDto> {
        val zSet = redissonClient.getScoredSortedSet<String>(
            IMMINENT_CONCERT_RANKING_KEY
        )

        // 오름차순 상위 5개 조회 (점수가 작을 수록 매진임박)
        val rankedConcertIds = zSet.valueRange(0, 4)
        if (rankedConcertIds.isEmpty()) {
            return listOf()
        }

        val concerts = concertJpaRepo.findByUuidIn(rankedConcertIds)
        val concertMap = concerts.associateBy { it.uuid }

        // TODO: 남은 좌석수 표시 여부 결정
        return rankedConcertIds.stream()
            .map { concertMap[it] }
            .map { it?.toDto() }
            .collect(Collectors.toList())
            .filterNotNull()
    }

    /**
     * 매진 임박 콘서트 업데이트 배치
     */
    @Scheduled(fixedRate = 300000) // 5분 스케줄링
    fun updateImminentConcertRanking() {
        val zSet = redissonClient.getScoredSortedSet<String>(
            IMMINENT_CONCERT_RANKING_KEY
        )

        // TODO: 매진 콘서트 별도 플래그 혹은 이미 지난 콘서트 유무 처리 필요
        val allConcerts = concertJpaRepo.findAll()

        allConcerts.forEach { concert ->

            val schedules = concertScheduleJpaRepo.findByConcertId(
                concert.uuid
            )

            // 남은 좌석수 합산
            var totalAvailableSeats = 0L
            schedules.forEach { schedule ->
                totalAvailableSeats += concertSeatJpaRepo.countByConcertScheduleIdAndStatus(
                    schedule.uuid,
                    ReservationStatus.AVAILABLE
                )
            }

            // Redis 랭킹 업데이트
            zSet.add(totalAvailableSeats.toDouble(), concert.uuid)

            // 매진이면 랭킹에서 제거
            if (totalAvailableSeats == 0L) {
                zSet.remove(concert.uuid)
            }
        }
    }
}
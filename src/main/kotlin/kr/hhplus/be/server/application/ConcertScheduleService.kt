package kr.hhplus.be.server.application

import kr.hhplus.be.server.infrastructure.persistence.ConcertScheduleJpaRepo
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertScheduleResponseDto
import org.springframework.stereotype.Service

@Service
class ConcertScheduleService(
    private val concertScheduleJpaRepo: ConcertScheduleJpaRepo
) {

    /**
     * 특정 콘서트 일정 목록 조회
     */
    fun getSchedules(concertId: String)
            : List<ConcertScheduleResponseDto> {
        val schedules = concertScheduleJpaRepo.findByConcertId(concertId)

        // 명시적으로 예외 처리
        if (schedules.isEmpty()) {
            throw IllegalArgumentException("콘서트를 찾을 수 없습니다: $concertId")
        }

        return schedules.map { it.toDto() }.toList()
    }

}
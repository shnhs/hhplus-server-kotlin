package kr.hhplus.be.server.application

import kr.hhplus.be.server.infrastructure.persistence.ConcertSeatJpaRepo
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertSeatResponseDto
import org.springframework.stereotype.Service

@Service
class ConcertSeatService(
    val concertSeatRepo: ConcertSeatJpaRepo
) {

    /**
     * 콘서트일정의 좌석 목록 조회
     */
    fun getScheduleSeats(concertScheduleId: String)
            : List<ConcertSeatResponseDto> {

        val seats = concertSeatRepo.findByConcertScheduleId(
            concertScheduleId = concertScheduleId
        )

        if (seats.isEmpty()) {
            throw IllegalArgumentException("콘서트 일정을 찾을 수 없습니다: $concertScheduleId")
        }

        return seats.map { it.toDto() }.toList()
    }

}
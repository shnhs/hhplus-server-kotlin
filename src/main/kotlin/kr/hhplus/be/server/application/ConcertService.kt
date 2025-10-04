package kr.hhplus.be.server.application

import kr.hhplus.be.server.entity.ConcertScheduleEntity
import kr.hhplus.be.server.infrastructure.persistence.ConcertJpaRepo
import kr.hhplus.be.server.infrastructure.persistence.ConcertScheduleJpaRepo
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertResponseDto
import org.springframework.stereotype.Service

@Service
class ConcertService(
    private val concertJpaRepo: ConcertJpaRepo,
    private val scheduleJpaRepo: ConcertScheduleJpaRepo
) {

    // 콘서트 일정 조회
    fun getAvailableConcertSchedules(concertId: String)
            : ConcertResponseDto {
        val concertEntity = concertJpaRepo.findByUuid(concertId)
            ?: throw IllegalStateException("존재하지 않는 콘서트 입니다.")

        val concertSchedules: List<ConcertScheduleEntity> = scheduleJpaRepo.findByConcertId(
            concertId = concertId
        )

        val availableSchedules = concertSchedules.map { it.concertDate }.toList()

        return concertEntity.toDto(availableSchedules)
    }
}
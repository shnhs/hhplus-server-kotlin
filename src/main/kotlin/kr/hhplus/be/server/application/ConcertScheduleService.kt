package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.ReservationStatus
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.infrastructure.persistence.ConcertScheduleJpaRepo
import kr.hhplus.be.server.interfaces.dto.ConcertScheduleDto.ConcertScheduleResponseDto
import org.springframework.stereotype.Service

@Service
class ConcertScheduleService(
    private val concertScheduleJpaRepo: ConcertScheduleJpaRepo,
    private val reservationRepo: ReservationRepo
) {
    // 예약가능한 콘서트 좌석 조회
    fun getAvailableSeats(scheduleId: String): ConcertScheduleResponseDto {
        val scheduleEntity = concertScheduleJpaRepo.findByUuid(uuid = scheduleId)
            ?: throw IllegalStateException("유효하지 않은 콘서트 일정입니다.")

        // 예약가능한 좌석 확인
        val reservations = reservationRepo.findByScheduleIdAndStatusIn(
            scheduleId = scheduleId,
            listOf(ReservationStatus.CONFIRMED, ReservationStatus.PENDING)
        )
        val availableSeats =
            (1..50).toList() - reservations.map { it.getSeatNumber() }.toSet()

        return scheduleEntity.toDto(availableSeats)
    }

}
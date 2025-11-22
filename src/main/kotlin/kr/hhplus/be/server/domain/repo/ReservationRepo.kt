package kr.hhplus.be.server.domain.repo


import jakarta.persistence.LockModeType
import kr.hhplus.be.server.domain.model.Reservation
import kr.hhplus.be.server.domain.model.ReservationStatus
import org.springframework.data.jpa.repository.Lock

interface ReservationRepo {
    fun save(reservation: Reservation): Reservation

    // 락 없는 일반 조회 (테스트, 단순 확인용)
    fun findByConcertIdAndScheduleIdAndSeatNumberWithoutLock(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): Reservation?

    // 락 걸고 조회 (예약용)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByConcertIdAndScheduleIdAndSeatNumber(
        concertId: String,
        scheduleId: String,
        seatNumber: Int
    ): Reservation

    fun findByUuid(uuid: String): Reservation?

    fun findExpiredReservation(): List<Reservation>

    fun findByScheduleIdAndStatusIn(
        scheduleId: String, status: List<ReservationStatus>
    ): List<Reservation>

    fun deleteAll()
}
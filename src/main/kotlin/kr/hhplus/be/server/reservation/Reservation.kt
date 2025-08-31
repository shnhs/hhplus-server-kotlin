package kr.hhplus.be.server.reservation

import jakarta.persistence.*
import kr.hhplus.be.server.common.BaseEntity
import kr.hhplus.be.server.concert.ConcertSchedule
import kr.hhplus.be.server.user.User
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "da_reservation")
class Reservation(
    val uuid: String = UUID.randomUUID().toString(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id", nullable = false)
    val concertSchedule: ConcertSchedule,

    @Enumerated(value = EnumType.STRING)
    val status: ReservationStatus,

    val date: LocalDateTime,

    val seatNumber: Int
) : BaseEntity() {
}

enum class ReservationStatus {
    PENDING, CONFIRMED
}



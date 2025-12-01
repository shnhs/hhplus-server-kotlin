package kr.hhplus.be.server.entity

import jakarta.persistence.*
import kr.hhplus.be.server.enums.ReservationStatus
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertSeatResponseDto
import org.hibernate.annotations.Comment
import java.util.*

@Entity
@Table(
    name = "bd_concert_seat"
)
class ConcertSeatEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true, updatable = false)
    var uuid: String = UUID.randomUUID().toString()

    @PrePersist
    fun generateUuid() {
        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString()
        }
    }

    @Column(nullable = false)
    @Comment(value = "콘서트 ID")
    var concertId: String = ""

    @Column(nullable = false)
    @Comment(value = "콘서트 일정 ID")
    var concertScheduleId: String = ""

    @Comment(value = "좌석번호")
    var seatNumber: Int = 0

    @Comment(value = "예약상태")
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.AVAILABLE

    fun toDto(): ConcertSeatResponseDto {
        return ConcertSeatResponseDto(
            concertId = this.concertId,
            scheduleId = this.concertScheduleId,
            seatNumber = this.seatNumber,
            status = this.status
        )
    }
}
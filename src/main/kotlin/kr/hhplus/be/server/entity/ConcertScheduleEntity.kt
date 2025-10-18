package kr.hhplus.be.server.entity

import jakarta.persistence.*
import kr.hhplus.be.server.interfaces.dto.ConcertScheduleDto.ConcertScheduleResponseDto
import org.hibernate.annotations.Comment
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "bc_concert_schedule")
class ConcertScheduleEntity {
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
    @Comment(value = "공연일자")
    var concertDate: LocalDateTime = LocalDateTime.now()

    @Comment(value = "예약가능한 좌석수")
    var availableSeats: Int = 50

    fun toDto(availableSeats: List<Int>): ConcertScheduleResponseDto {
        return ConcertScheduleResponseDto(
            scheduleId = this.uuid,
            concertId = this.concertId,
            concertDate = this.concertDate,
            availableSeats = availableSeats
        )
    }
}
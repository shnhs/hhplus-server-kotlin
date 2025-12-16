package kr.hhplus.be.server.entity

import jakarta.persistence.*
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertScheduleResponseDto
import org.hibernate.annotations.Comment
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(
    name = "bc_concert_schedule",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["concertId", "concertDate"])
    ]
)
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

    fun toDto(): ConcertScheduleResponseDto {
        return ConcertScheduleResponseDto(
            scheduleId = this.uuid,
            concertId = this.concertId,
            concertDate = this.concertDate
        )
    }
}
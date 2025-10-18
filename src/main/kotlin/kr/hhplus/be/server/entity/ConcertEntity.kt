package kr.hhplus.be.server.entity

import jakarta.persistence.*
import kr.hhplus.be.server.interfaces.dto.ConcertDto.ConcertResponseDto
import org.hibernate.annotations.Comment
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "bb_concert")
class ConcertEntity {

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
    @Comment(value = "콘서트 이름")
    var concertName: String = ""

    @Column(nullable = false)
    @Comment(value = "아티스트")
    var artist: String = ""

    @Column(nullable = false)
    @Comment(value = "콘서트 홀")
    var concertHall: String = ""

    fun toDto(availableSchedules: List<LocalDateTime>)
            : ConcertResponseDto {
        return ConcertResponseDto(
            concertId = this.uuid,
            concertName = this.concertName,
            artist = this.artist,
            concertHall = this.concertHall,
            availableSchedules = availableSchedules
        )
    }
}
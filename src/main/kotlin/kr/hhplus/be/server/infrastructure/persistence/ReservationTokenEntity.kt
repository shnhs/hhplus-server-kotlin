package kr.hhplus.be.server.infrastructure.persistence

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "da_reservation_token")
class ReservationTokenEntity {
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
    @Comment("사용자 ID")
    var userId: String = ""

    @Column(nullable = false)
    @Comment("토큰 발급일자")
    var createdAt: LocalDateTime = LocalDateTime.now()
}
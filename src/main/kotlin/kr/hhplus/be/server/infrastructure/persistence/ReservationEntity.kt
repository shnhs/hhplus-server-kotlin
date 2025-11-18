package kr.hhplus.be.server.infrastructure.persistence

import jakarta.persistence.*
import kr.hhplus.be.server.domain.model.ReservationStatus
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "db_reservation")
class ReservationEntity {
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
    var concertId: String = ""

    @Column(nullable = false)
    var scheduleId: String = ""

    @Column(nullable = false)
    var seatNumber: Int = 0

    @Column(nullable = true)
    var userId: String? = null

    @Column(nullable = true)
    var reservedAt: LocalDateTime? = null

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.PENDING
}

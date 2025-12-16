package kr.hhplus.be.server.infrastructure.persistence

import jakarta.persistence.*
import kr.hhplus.be.server.enums.ReservationStatus
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
    var scheduleId: String = ""

    @Column(nullable = false)
    var seatId: String = ""

    @Column(nullable = false)
    var userId: String = ""

    @Column(nullable = false)
    var reservedAt: LocalDateTime = LocalDateTime.now()

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.PENDING
}

package kr.hhplus.be.server.concert

import jakarta.persistence.*
import kr.hhplus.be.server.common.BaseEntity
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "bb_concert_schedules")
class ConcertSchedule(
    val uuid: String = UUID.randomUUID().toString(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "concert_id", nullable = false)
    val concert: Concert,

    val availableSeats: Int = 50,

    val concertDate: LocalDateTime,

    ) : BaseEntity() {}
package kr.hhplus.be.server

import kr.hhplus.be.server.entity.ConcertEntity
import kr.hhplus.be.server.entity.ConcertScheduleEntity
import java.time.LocalDateTime
import java.util.*

class Fixtures {

    fun concert(concertName: String, concertHall: String, artist: String): ConcertEntity {
        return ConcertEntity().apply {
            this.uuid = UUID.randomUUID().toString()
            this.concertName = concertName
            this.concertHall = concertHall
            this.artist = artist
        }
    }

    fun concertSchedule(
        concertId: String,
        startDate: LocalDateTime,
        performanceCount: Int,
        seatCount: Int
    ): List<ConcertScheduleEntity> {
        val schedules = mutableListOf<ConcertScheduleEntity>()

        repeat(performanceCount) { dayOffset ->
            val concertDate = startDate.plusDays(dayOffset.toLong())

            repeat(seatCount) { seatNumber ->
                schedules.add(
                    ConcertScheduleEntity().apply {
                        this.concertId = concertId
                        this.concertDate = concertDate
                    }
                )
            }
        }

        return schedules
    }

}
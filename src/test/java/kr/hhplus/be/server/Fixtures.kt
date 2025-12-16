package kr.hhplus.be.server

import kr.hhplus.be.server.entity.ConcertEntity
import kr.hhplus.be.server.entity.ConcertScheduleEntity
import kr.hhplus.be.server.entity.ConcertSeatEntity
import kr.hhplus.be.server.enums.ReservationStatus
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
        concertDays: Int,
        maxSeatNumber: Int
    ): List<ConcertScheduleEntity> {
        val schedules = mutableListOf<ConcertScheduleEntity>()

        repeat(concertDays) { dayOffset ->
            val concertDate = startDate.plusDays(dayOffset.toLong())

            repeat(maxSeatNumber) { seatNumber ->
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

    fun concertSeats(
        concertId: String, concertScheduleId: String, maxSeatNumber: Int
    ): List<ConcertSeatEntity> {
        return (1..maxSeatNumber).map {
            ConcertSeatEntity().apply {
                this.concertId = concertId
                this.concertScheduleId = concertScheduleId
                this.seatNumber = it
            }
        }
    }

    fun concertSeat(
        concertId: String,
        concertScheduleId: String,
        seatNumber: Int,
        status: ReservationStatus
    ): ConcertSeatEntity {
        return ConcertSeatEntity(concertId, concertScheduleId, seatNumber, status)
    }

}
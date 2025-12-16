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

    @Column(nullable = false)
    @Comment(value = "예약상태")
    @Enumerated(EnumType.STRING)
    var status: ReservationStatus = ReservationStatus.AVAILABLE
        private set  // setter를 private으로 제한

    // 상태 변경 메서드
    fun reserve() {
        if (this.status != ReservationStatus.AVAILABLE) {
            throw IllegalStateException("이미 선택된 좌석입니다.")
        }
        this.status = ReservationStatus.PENDING
    }

    fun confirm() {
        if (this.status != ReservationStatus.PENDING) {
            throw IllegalStateException("예약 상태가 아닙니다.")
        }
        this.status = ReservationStatus.CONFIRMED
    }

    fun release() {
        this.status = ReservationStatus.AVAILABLE
    }


    fun toDto(): ConcertSeatResponseDto {
        return ConcertSeatResponseDto(
            seatId = this.uuid,
            concertId = this.concertId,
            scheduleId = this.concertScheduleId,
            seatNumber = this.seatNumber,
            status = this.status
        )
    }


    constructor()

    // 테스트용 생성자
    constructor(
        concertId: String,
        concertScheduleId: String,
        seatNumber: Int,
        status: ReservationStatus
    ) {
        this.concertId = concertId
        this.concertScheduleId = concertScheduleId
        this.seatNumber = seatNumber
        this.status = status  // 생성자 내부에서는 private set 무시됨
    }
}


package kr.hhplus.be.server.history

import jakarta.persistence.*
import kr.hhplus.be.server.common.BaseEntity
import kr.hhplus.be.server.reservation.Reservation
import java.util.*

@Entity
@Table(name = "ea_payment_history")
class PaymentHistory(

    val uuid: String = UUID.randomUUID().toString(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    val reservation: Reservation

) : BaseEntity() {

}
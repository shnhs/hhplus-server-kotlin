package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.Payment
import kr.hhplus.be.server.domain.repo.PaymentRepo
import kr.hhplus.be.server.domain.repo.ReservationRepo
import kr.hhplus.be.server.domain.repo.UserRepo
import org.springframework.stereotype.Service

@Service
class PaymentService(
    private val paymentRepo: PaymentRepo,
    private val reservationRepo: ReservationRepo,
    private val userRepo: UserRepo
) {
    // 결제 요청
    fun postPayment(reservationId: String): Payment {
        //  결제대기중인 예약인지 확인
        val reservation = (reservationRepo.findByUuid(reservationId)
            ?: throw IllegalStateException("만료되었거나 이미 결제된 예약입니다."))

        // 사용자 포인트
        val userId = reservation.getUserId()
        val user = userRepo.findByUuid(userId)
        user!!.usePoint(50000) // TODO 티켓가격로직 설정 필요

        reservation.confirm() // 예약확정 처리

        val payment = Payment.create(
            reservationId = reservationId,
            userId = userId
        )

        return paymentRepo.save(payment = payment)
    }
}
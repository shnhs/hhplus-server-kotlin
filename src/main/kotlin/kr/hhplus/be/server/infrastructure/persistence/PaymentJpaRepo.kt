package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.domain.model.Payment
import kr.hhplus.be.server.domain.repo.PaymentRepo
import org.springframework.data.jpa.repository.JpaRepository

interface SpringPaymentJpa : JpaRepository<PaymentEntity, Long> {
    fun findByUuid(uuid: String): PaymentEntity?
}


class PaymentJpaRepo(
    private val jpa: SpringPaymentJpa
) : PaymentRepo {
    override fun save(payment: Payment): Payment {
        val entity = toEntity(payment)
        jpa.save(entity)
        return payment
    }

    override fun findByUuid(uuid: String): Payment? {
        val entity = jpa.findByUuid(uuid)
        return entity?.let { toDomain(it) }
    }

    // Domain -> JpaEntity 매핑
    private fun toEntity(payment: Payment): PaymentEntity {
        return PaymentEntity().apply {
            uuid = payment.getPaymentId()
            reservationId = payment.getReservationId()
            userId = payment.getUserId()
            createdAt = payment.getCreatedAt()
        }
    }

    // JpaEntity -> Domain 매핑
    private fun toDomain(entity: PaymentEntity): Payment {
        return Payment.createWithUuid(
            paymentId = entity.uuid,
            reservationId = entity.reservationId,
            userId = entity.userId,
            createdAt = entity.createdAt
        )
    }
}
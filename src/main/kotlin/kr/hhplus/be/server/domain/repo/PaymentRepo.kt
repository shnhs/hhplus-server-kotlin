package kr.hhplus.be.server.domain.repo

import kr.hhplus.be.server.domain.model.Payment

interface PaymentRepo {

    fun save(payment: Payment): Payment

    fun findByUuid(uuid: String): Payment?
}
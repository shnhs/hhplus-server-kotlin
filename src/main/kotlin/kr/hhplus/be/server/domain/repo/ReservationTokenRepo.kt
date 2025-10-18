package kr.hhplus.be.server.domain.repo

import kr.hhplus.be.server.domain.model.ReservationToken

interface ReservationTokenRepo {
    fun save(reservationToken: ReservationToken): ReservationToken
    fun findByUuid(uuid: String): ReservationToken
    fun findByUserId(userId: String): ReservationToken?
    fun delete(reservationToken: ReservationToken): ReservationToken
}
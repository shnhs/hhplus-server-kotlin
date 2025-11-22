package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.ReservationToken
import kr.hhplus.be.server.domain.repo.ReservationTokenRepo
import org.springframework.stereotype.Service

@Service
class ReservationTokenService(
    private val reservationTokenRepo: ReservationTokenRepo
) {
    // 대기열 토큰 발급
    fun postReservationToken(userId: String): ReservationToken {
        // 유효한 토큰이 이미 있다면 리턴
        reservationTokenRepo.findByUserId(userId)?.let { it ->
            if (!it.isExpired()) {
                return it // 유효하면 그대로 반환
            }
            // 만료됐으면 삭제
            reservationTokenRepo.delete(it)
        }

        // 유효한 토큰이 없으면 새로 생성
        val create = ReservationToken.create(
            userId = userId
        )

        val save = reservationTokenRepo.save(create)
        return save
    }
}
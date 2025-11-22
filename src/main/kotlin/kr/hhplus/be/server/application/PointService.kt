package kr.hhplus.be.server.application

import kr.hhplus.be.server.domain.model.User
import kr.hhplus.be.server.domain.repo.UserRepo
import org.springframework.stereotype.Service

@Service
class PointService(private val userRepo: UserRepo) {

    // 포인트 충전
    fun chargePoint(userId: String, amount: Int): User {
        val user = (userRepo.findByUuid(userId)
            ?: throw IllegalArgumentException("존재하지 않는 사용자 입니다."))

        user.chargePoint(amount)

        return userRepo.save(user)
    }
}
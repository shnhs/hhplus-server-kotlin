package kr.hhplus.be.server.domain.model

import java.util.*

class User(
    private val userId: String,
    private val userName: String,
    private val email: String
) {
    private var point: Int = 0

    companion object {
        fun create(
            userName: String,
            email: String
        ): User {
            return User(
                userId = UUID.randomUUID().toString(),
                userName = userName,
                email = email
            )
        }

        fun createWithUuid(
            userId: String,
            userName: String,
            email: String,
            point: Int
        ): User {
            return User(
                userId = userId,
                userName = userName,
                email = email
            ).apply {
                this.point = point
            }
        }
    }

    // 유저 포인트 충전
    fun chargePoint(amount: Int) {
        if (amount <= 0) {
            throw IllegalArgumentException("유효하지 않은 입력입니다.")
        }
        this.point += amount
    }

    // 유저 포인트 사용
    fun usePoint(amount: Int) {
        if (amount > this.point) {
            throw IllegalArgumentException("포인트가 부족합니다.")
        }
        this.point -= amount
    }

    // getters
    fun getUserId(): String = userId
    fun getUserName(): String = userName
    fun getEmail(): String = email
    fun getPoint(): Int = point
}
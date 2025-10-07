package kr.hhplus.be.server.domain.repo

import kr.hhplus.be.server.domain.model.User

interface UserRepo {
    fun save(user: User): User

    fun findByUuid(uuid: String): User?
}
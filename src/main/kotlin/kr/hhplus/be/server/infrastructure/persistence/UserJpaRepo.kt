package kr.hhplus.be.server.infrastructure.persistence

import kr.hhplus.be.server.domain.model.User
import kr.hhplus.be.server.domain.repo.UserRepo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


interface SpringUserJpa : JpaRepository<UserEntity, Long> {
    fun findByUuid(uuid: String): UserEntity?
}

@Repository
class UserJpaRepo(
    private val jpa: SpringUserJpa
) : UserRepo {
    override fun save(user: User): User {
        val entity = toEntity(user)
        jpa.save(entity)
        return user
    }

    override fun findByUuid(uuid: String): User? {
        val entity = jpa.findByUuid(uuid)
        return entity?.let { toDomain(it) }
    }

    private fun toEntity(user: User): UserEntity {
        return UserEntity().apply {
            uuid = user.getUserId()
            userName = user.getUserName()
            email = user.getEmail()
            point = user.getPoint()
        }
    }

    private fun toDomain(entity: UserEntity): User {
        return User.createWithUuid(
            userId = entity.uuid,
            userName = entity.userName,
            email = entity.email,
            point = entity.point
        )
    }
}
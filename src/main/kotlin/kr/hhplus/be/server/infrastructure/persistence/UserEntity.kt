package kr.hhplus.be.server.infrastructure.persistence

import jakarta.persistence.*
import org.hibernate.annotations.Comment
import java.util.*

@Entity
@Table(name = "aa_user")
class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(nullable = false, unique = true, updatable = false)
    var uuid: String = UUID.randomUUID().toString()

    @PrePersist
    fun generateUuid() {
        if (uuid.isEmpty()) {
            uuid = UUID.randomUUID().toString()
        }
    }

    @Column(nullable = false)
    @Comment(value = "사용자 이름")
    var userName: String = ""

    @Column(nullable = false)
    @Comment(value = "사용자 이메일")
    var email: String = ""

    @Comment(value = "사용자 포인트")
    var point: Int = 0
}
package kr.hhplus.be.server.queuetoken

import jakarta.persistence.*
import kr.hhplus.be.server.common.BaseEntity
import kr.hhplus.be.server.user.User
import java.util.*

@Entity
@Table(name = "ca_queue_tokens")
class QueueToken(
    val uuid: String = UUID.randomUUID().toString(),

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    val user: User,

    @Enumerated(EnumType.STRING)
    val status: QueueTokenStatus,
) : BaseEntity() {

}

enum class QueueTokenStatus {
    WAIT, ACTIVE, EXPIRED
}
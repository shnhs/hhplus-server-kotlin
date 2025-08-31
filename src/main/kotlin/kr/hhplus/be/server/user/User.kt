package kr.hhplus.be.server.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import kr.hhplus.be.server.common.BaseEntity
import java.util.*

@Entity
@Table(name = "aa_user", schema = "hhplus")
class User(
    val uuid: String = UUID.randomUUID().toString(),

    @Column(nullable = false, length = 100)
    var name: String,

    @Column(unique = true, nullable = false, length = 100)
    var email: String,

    @Column(nullable = false)
    var point: Long
) : BaseEntity() {

}
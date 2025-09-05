package kr.hhplus.be.server.common

import jakarta.persistence.*
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @CreatedDate
    @Column(updatable = false, nullable = false)
    var createdAt: LocalDateTime? = null,

    @CreatedBy
    @Column(updatable = false)
    var createdBy: String? = null,

    @LastModifiedDate
    @Column(nullable = true)
    var updatedAt: LocalDateTime? = null,

    @LastModifiedBy
    var updatedBy: String? = null
)
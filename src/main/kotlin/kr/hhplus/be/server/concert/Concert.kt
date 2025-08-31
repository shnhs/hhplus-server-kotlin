package kr.hhplus.be.server.concert

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import kr.hhplus.be.server.common.BaseEntity
import java.util.*

@Entity
@Table(name = "ba_concerts")
class Concert(
    val uuid: String = UUID.randomUUID().toString(),

    val name: String,

    val artist: String,

    val concertHall: String,

    @OneToMany(mappedBy = "concert", cascade = [CascadeType.ALL], orphanRemoval = true)
    val schedules: MutableList<ConcertSchedule> = mutableListOf()
) : BaseEntity() {

}
package kr.hs.dsm.inq.domain.user.persistence

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table

@Table(name = "tbl_user")
@Entity
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    val username: String,

    val job: String,

    val jobDuration: Int,

    val accountId: String,

    val password: String
)
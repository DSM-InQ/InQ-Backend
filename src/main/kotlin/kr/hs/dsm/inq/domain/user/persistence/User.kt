package kr.hs.dsm.inq.domain.user.persistence

import kr.hs.dsm.inq.domain.user.presentation.dto.UpdateUserInfoRequest
import java.time.LocalDate
import javax.persistence.Column
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

    @Column(nullable = false)
    var username: String,

    @Column(nullable = false)
    var job: String,

    @Column(columnDefinition = "INT", nullable = false)
    var jobDuration: Int,

    @Column(nullable = false)
    var accountId: String,

    @Column(nullable = false)
    var password: String,

    @Column(nullable = false)
    val joinDate: LocalDate,

    @Column(columnDefinition = "INT", nullable = false)
    var coin: Int = 0
) {

    fun updateInfo(request: UpdateUserInfoRequest) {
        this.username = request.username
        this.job = request.job
        this.jobDuration = request.jobDuration
    }
}
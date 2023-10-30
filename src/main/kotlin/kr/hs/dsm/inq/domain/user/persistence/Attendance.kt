package kr.hs.dsm.inq.domain.user.persistence

import javax.persistence.*

@Table(name = "tbl_attendance")
@Entity
class Attendance (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BIGINT", nullable = false)
    val user: User,

    var monday: Boolean = false,

    var tuesday: Boolean = false,

    var wednesday: Boolean = false,

    var thursday: Boolean = false,

    var friday: Boolean = false,

    var saturday: Boolean = false,

    var sunday: Boolean = false,
)
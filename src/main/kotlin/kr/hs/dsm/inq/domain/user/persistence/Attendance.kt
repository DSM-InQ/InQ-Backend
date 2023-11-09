package kr.hs.dsm.inq.domain.user.persistence

import java.time.LocalDate
import javax.persistence.*

@Table(name = "tbl_attendance")
@Entity
class Attendance (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

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
) {
    fun attendanceCheck(today: String) {
        when (today) {
            "monday" -> monday = true
            "tuesday" -> tuesday = true
            "wednesday" -> wednesday = true
            "thursday" -> thursday = true
            "friday" -> friday = true
            "saturday" -> saturday = true
            "sunday" -> sunday = true
        }
    }

    fun initializeAttendance() {
        monday = false
        tuesday = false
        wednesday = false
        thursday = false
        friday = false
        saturday = false
        sunday = false
    }
}
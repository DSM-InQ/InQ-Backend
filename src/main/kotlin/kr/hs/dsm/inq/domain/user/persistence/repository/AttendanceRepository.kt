package kr.hs.dsm.inq.domain.user.persistence.repository

import kr.hs.dsm.inq.domain.user.persistence.Attendance
import org.springframework.data.repository.CrudRepository

interface AttendanceRepository : CrudRepository<Attendance, Long> {
    fun findAllByUserId(userId: Long): List<Attendance>?
}
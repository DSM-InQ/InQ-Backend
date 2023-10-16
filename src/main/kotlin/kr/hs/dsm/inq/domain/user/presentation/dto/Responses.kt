package kr.hs.dsm.inq.domain.user.presentation.dto

import java.time.LocalDate

data class UserInfoResponse(
    val username: String,
    val joinDate: LocalDate,
    val coin: Int,
    val job: String,
    val jobDuration: Int,
    val attendanceCheckList: List<AttendanceCheck>
)

data class  AttendanceCheck(
    val monday: Boolean,
    val tuesday: Boolean,
    val wednesday: Boolean,
    val thursday: Boolean,
    val friday: Boolean,
    val saturday: Boolean,
    val sunday: Boolean
)
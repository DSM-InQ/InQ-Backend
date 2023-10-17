package kr.hs.dsm.inq.domain.user.presentation.dto

import java.time.LocalDate

data class UserInfoResponse(
    val username: String,
    val joinDate: LocalDate,
    val coin: Int,
    val job: String,
    val jobDuration: Int
)
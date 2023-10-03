package kr.hs.dsm.inq.domain.user.presentation.dto.request

import javax.validation.constraints.NotBlank

class UserSignUpRequest (

    @field:NotBlank
    val accountId: String,

    @field:NotBlank
    val userName: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val job: String,

    @field:NotBlank
    val jobDuration: Int
)
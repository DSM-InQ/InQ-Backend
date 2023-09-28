package kr.hs.dsm.inq.domain.user.presentation.dto.request

import javax.validation.constraints.NotBlank

class UserSignInRequest (

    @field:NotBlank
    val accountId: String,

    @field:NotBlank
    val password: String
)
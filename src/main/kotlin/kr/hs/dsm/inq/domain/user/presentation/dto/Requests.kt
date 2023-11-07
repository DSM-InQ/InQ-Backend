package kr.hs.dsm.inq.domain.user.presentation.dto

import javax.validation.constraints.NotBlank

data class UserSignInRequest(

    @field:NotBlank
    val accountId: String,

    @field:NotBlank
    val password: String
)

data class UserSignUpRequest(

    @field:NotBlank
    val accountId: String,

    @field:NotBlank
    val username: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val job: String,

    val jobDuration: Int
)

data class UpdateUserInfoRequest(

    @field:NotBlank
    val username: String,

    @field:NotBlank
    val job: String,

    val jobDuration: Int
)

data class GetMyQuestionRequest(
    val page: Int
)
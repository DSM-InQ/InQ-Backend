package kr.hs.dsm.inq.domain.user.presentation

import kr.hs.dsm.inq.common.dto.TokenResponse
import kr.hs.dsm.inq.domain.user.presentation.dto.request.UserSignInRequest
import kr.hs.dsm.inq.domain.user.presentation.dto.request.UserSignUpRequest
import kr.hs.dsm.inq.domain.user.service.UserSignInService
import kr.hs.dsm.inq.domain.user.service.UserSignUpService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController("/user")
class UserController(
    private val userSignUpService: UserSignUpService,
    private val userSignInService: UserSignInService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun userSignUp(@RequestBody request: UserSignUpRequest) {
        userSignUpService.signUp(request)
    }

    @PostMapping("/auth")
    fun userSignIn(@RequestBody request: UserSignInRequest): TokenResponse =
        userSignInService.signIn(request)
}
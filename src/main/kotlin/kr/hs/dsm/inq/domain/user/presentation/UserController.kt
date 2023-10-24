package kr.hs.dsm.inq.domain.user.presentation

import kr.hs.dsm.inq.common.dto.TokenResponse
import kr.hs.dsm.inq.domain.user.presentation.dto.UpdateUserInfoRequest
import kr.hs.dsm.inq.domain.user.presentation.dto.UserInfoResponse
import kr.hs.dsm.inq.domain.user.presentation.dto.UserSignInRequest
import kr.hs.dsm.inq.domain.user.presentation.dto.UserSignUpRequest
import kr.hs.dsm.inq.domain.user.service.UserService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/user")
@RestController
class UserController(
    private val userService: UserService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    fun userSignUp(@RequestBody request: UserSignUpRequest) {
        userService.signUp(request)
    }

    @PostMapping("/auth")
    fun userSignIn(@RequestBody request: UserSignInRequest): TokenResponse =
        userService.signIn(request)

    @GetMapping("/profile")
    fun queryUserInfo(): UserInfoResponse =
        userService.queryUserInfo()

    @PutMapping("/profile")
    fun updateUserInfo(@RequestBody request: UpdateUserInfoRequest) {
        userService.updateUserInfo(request)
    }

}
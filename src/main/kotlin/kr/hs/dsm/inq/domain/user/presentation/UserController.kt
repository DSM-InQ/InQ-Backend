package kr.hs.dsm.inq.domain.user.presentation

import javax.validation.Valid
import kr.hs.dsm.inq.common.dto.TokenResponse
import kr.hs.dsm.inq.domain.user.presentation.dto.*
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
    fun userSignUp(@Valid @RequestBody request: UserSignUpRequest) {
        userService.signUp(request)
    }

    @PostMapping("/auth")
    fun userSignIn(@Valid @RequestBody request: UserSignInRequest): TokenResponse =
        userService.signIn(request)

    @GetMapping("/profile")
    fun queryUserInfo(): UserInfoResponse =
        userService.queryUserInfo()

    @PutMapping("/profile")
    fun updateUserInfo(@Valid @RequestBody request: UpdateUserInfoRequest) {
        userService.updateUserInfo(request)
    }

    @GetMapping("/check")
    fun queryUserAttendance(): UserAttendanceResponse =
        userService.queryUserAttendance()

}
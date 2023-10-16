package kr.hs.dsm.inq.domain.user.service

import kr.hs.dsm.inq.common.dto.TokenResponse
import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.domain.user.exception.AttendanceNotFound
import kr.hs.dsm.inq.domain.user.exception.PasswordMismatchException
import kr.hs.dsm.inq.domain.user.exception.UserAlreadyExist
import kr.hs.dsm.inq.domain.user.exception.UserNotFound
import kr.hs.dsm.inq.domain.user.persistence.User
import kr.hs.dsm.inq.domain.user.persistence.repository.AttendanceRepository
import kr.hs.dsm.inq.domain.user.persistence.repository.UserRepository
import kr.hs.dsm.inq.domain.user.presentation.dto.*
import kr.hs.dsm.inq.global.security.token.JwtGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDate
import javax.transaction.Transactional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtGenerator: JwtGenerator,
    private val attendanceRepository: AttendanceRepository
) {

    fun signIn(request: UserSignInRequest): TokenResponse {
        val user = userRepository.findByAccountId(request.accountId)
            ?: throw UserNotFound

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw PasswordMismatchException
        }

        return jwtGenerator.receiveToken(user.id)
    }

    fun signUp(request: UserSignUpRequest) {

        if (userRepository.existsByAccountId(request.accountId)) {
            throw UserAlreadyExist
        }

        userRepository.save(
            User(
                username = request.username,
                job = request.job,
                jobDuration = request.jobDuration,
                accountId = request.accountId,
                password = passwordEncoder.encode(request.password),
                joinDate = LocalDate.now()
            )
        )
    }

    fun queryUserInfo(): UserInfoResponse {

        val user = SecurityUtil.getCurrentUser()

        val attendanceCheckList = attendanceRepository.findAllByUserId(user.id)
            ?: throw AttendanceNotFound

        return UserInfoResponse(
            username = user.username,
            joinDate = user.joinDate,
            coin = user.coin,
            job = user.job,
            jobDuration = user.jobDuration,
            attendanceCheckList = attendanceCheckList.map {
                AttendanceCheck(
                    monday = it.monday,
                    tuesday = it.tuesday,
                    wednesday = it.wednesday,
                    thursday = it.thursday,
                    friday = it.friday,
                    saturday = it.saturday,
                    sunday = it.sunday
                )
            }
        )
    }

    @Transactional
    fun updateUserInfo(request: UpdateUserInfoRequest) {

        val user = SecurityUtil.getCurrentUser()

        user.updateInfo(request)
    }
}
package kr.hs.dsm.inq.domain.user.service

import kr.hs.dsm.inq.common.dto.TokenResponse
import kr.hs.dsm.inq.domain.user.exception.PasswordMismatchException
import kr.hs.dsm.inq.domain.user.exception.UserNotFound
import kr.hs.dsm.inq.domain.user.persistence.UserRepository
import kr.hs.dsm.inq.domain.user.presentation.dto.request.UserSignInRequest
import kr.hs.dsm.inq.global.security.token.JwtGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserSignInService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtGenerator: JwtGenerator
) {

    fun signIn(request: UserSignInRequest): TokenResponse {
        val user = userRepository.findByAccountId(request.accountId)
            ?: throw UserNotFound

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw PasswordMismatchException
        }

        return jwtGenerator.receiveToken(user.id)
    }
}
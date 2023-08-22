package kr.hs.dsm.inq.common.dto

import java.time.LocalDateTime

data class TokenResponse(
    val accessToken: String,
    val accessTokenExpiredAt: LocalDateTime
)
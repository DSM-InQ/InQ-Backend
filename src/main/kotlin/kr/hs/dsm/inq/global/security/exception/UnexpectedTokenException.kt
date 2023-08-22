package kr.hs.dsm.inq.global.security.exception

import kr.hs.dsm.inq.common.error.CustomException
import kr.hs.dsm.inq.global.error.SecurityErrorCode

object UnexpectedTokenException : CustomException(
    SecurityErrorCode.UNEXPECTED_TOKEN
)

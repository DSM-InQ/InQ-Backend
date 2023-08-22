package kr.hs.dsm.inq.global.security.exception

import kr.hs.dsm.inq.common.error.CustomException
import kr.hs.dsm.inq.global.error.SecurityErrorCode

object ForbiddenException : CustomException(
    SecurityErrorCode.FORBIDDEN
)

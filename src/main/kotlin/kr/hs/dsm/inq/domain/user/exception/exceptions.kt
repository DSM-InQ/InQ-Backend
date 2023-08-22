package kr.hs.dsm.inq.domain.user.exception

import kr.hs.dsm.inq.common.error.CustomException
import kr.hs.dsm.inq.global.error.DomainErrorCode


object UserNotFound : CustomException(
    DomainErrorCode.USER_NOT_FOUND
)

object PasswordMismatchException : CustomException(
    DomainErrorCode.PASSWORD_MISMATCH
)
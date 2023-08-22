package kr.hs.dsm.inq.global.error

import kr.hs.dsm.inq.common.error.CustomException

object InternalServerError : CustomException(
    GlobalErrorCode.INTERNAL_SERVER_ERROR
)
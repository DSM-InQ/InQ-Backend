package kr.hs.dsm.inq.common.util

import kr.hs.dsm.inq.global.security.principle.CustomDetails
import org.springframework.security.core.context.SecurityContextHolder

object SecurityUtil {
    fun getCurrentUser() =
        (SecurityContextHolder.getContext().authentication.principal as CustomDetails).user
}
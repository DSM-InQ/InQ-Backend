package kr.hs.dsm.inq.global.error

import kr.hs.dsm.inq.common.error.ErrorProperty
import kr.hs.dsm.inq.common.error.ErrorStatus

enum class DomainErrorCode(
    private val status: Int,
    private val message: String,
    private val sequence: Int
) : ErrorProperty {

    PASSWORD_MISMATCH(ErrorStatus.FORBIDDEN, "Password mismatched", 1),

    USER_NOT_FOUND(ErrorStatus.BAD_REQUEST, "User Not Found", 1),
    ;

    override fun status(): Int = status
    override fun message(): String = message
    override fun code(): String = "DOMAIN-$status-$sequence"
}

enum class SecurityErrorCode(
    private val status: Int,
    private val message: String,
    private val sequence: Int
) : ErrorProperty {

    INVALID_TOKEN(ErrorStatus.UNAUTHORIZED, "Invalid Token", 1),
    EXPIRED_TOKEN(ErrorStatus.UNAUTHORIZED, "Expired Token", 2),
    UNEXPECTED_TOKEN(ErrorStatus.UNAUTHORIZED, "Unexpected Token", 3),
    INVALID_ROLE(ErrorStatus.UNAUTHORIZED, "Invalid Role", 4),

    FORBIDDEN(ErrorStatus.FORBIDDEN, "Can Not Access", 1);

    override fun status(): Int = status
    override fun message(): String = message
    override fun code(): String = "SECURITY-$status-$sequence"
}

enum class GlobalErrorCode(
    private val status: Int,
    private val message: String,
    private val sequence: Int
) : ErrorProperty {

    BAD_REQUEST(ErrorStatus.BAD_REQUEST, "Bad request", 1),

    INTERNAL_SERVER_ERROR(ErrorStatus.INTERNAL_SERVER_ERROR, "Internal server error", 1)
    ;

    override fun status(): Int = status
    override fun message(): String = message
    override fun code(): String = "GLOBAL-$status-$sequence"
}
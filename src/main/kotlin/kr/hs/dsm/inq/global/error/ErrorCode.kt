package kr.hs.dsm.inq.global.error

import kr.hs.dsm.inq.common.error.ErrorProperty
import kr.hs.dsm.inq.common.error.ErrorStatus

enum class DomainErrorCode(
    private val status: Int,
    private val message: String,
    private val sequence: Int
) : ErrorProperty {

    PASSWORD_MISMATCH(ErrorStatus.FORBIDDEN, "Password mismatched", 1),

    USER_NOT_FOUND(ErrorStatus.NOT_FOUND, "User Not Found", 1),
    QUESTION_NOT_FOUND(ErrorStatus.NOT_FOUND, "Question Not Found", 2),
    ANSWER_NOT_FOUND(ErrorStatus.NOT_FOUND, "Answer Not Found", 3),
    TAG_NOT_FOUND(ErrorStatus.NOT_FOUND, "Tag Not Found", 4),
    ATTENDANCE_NOT_FOUND(ErrorStatus.NOT_FOUND, "Attendance Not Found", 5),
    QUESTION_SET_NOT_FOUND(ErrorStatus.NOT_FOUND, "Question Set Not Found", 6),

    ALREADY_LIKED_POST(ErrorStatus.CONFLICT, "Already liked post", 1),
    ALREADY_DISLIKED_POST(ErrorStatus.CONFLICT, "Already disliked post", 2),
    ALREADY_USER_EXIST(ErrorStatus.CONFLICT, "Already User Exist", 3)
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

    INVALID_TOKEN(ErrorStatus.UNAUTHORIZED, "Invalid token", 1),
    EXPIRED_TOKEN(ErrorStatus.UNAUTHORIZED, "Expired token", 2),
    UNEXPECTED_TOKEN(ErrorStatus.UNAUTHORIZED, "Unexpected token", 3),
    INVALID_ROLE(ErrorStatus.UNAUTHORIZED, "Invalid role", 4),

    FORBIDDEN(ErrorStatus.FORBIDDEN, "Can not access", 1);

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
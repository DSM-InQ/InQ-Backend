package kr.hs.dsm.inq.domain.question.exception

import kr.hs.dsm.inq.common.error.CustomException
import kr.hs.dsm.inq.global.error.DomainErrorCode


object QuestionNotFoundException : CustomException(
    DomainErrorCode.QUESTION_NOT_FOUND
)

object AnswerNotFoundException : CustomException(
    DomainErrorCode.ANSWER_NOT_FOUND
)

object AlreadyLikedPostException : CustomException(
    DomainErrorCode.ALREADY_LIKED_POST
)

object AlreadyDislikedPostException : CustomException(
    DomainErrorCode.ALREADY_DISLIKED_POST
)
package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.ProblemType
import java.time.LocalDateTime

class QuestionSetUserSolvedDto @QueryProjection constructor(
    val problemType: ProblemType,
    val questionSetId: Long,
    val questionSetName: String,
    val solvedAt: LocalDateTime,
)
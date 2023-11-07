package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.ProblemType
import kr.hs.dsm.inq.domain.question.persistence.Tags
import java.time.LocalDateTime

class QuestionSetDetailsUserSolved @QueryProjection constructor (
    val problemType: ProblemType,
    val questionSetId: Long,
    val questionId: Long,
    val question: String,
    val category: Category,
    val username: String,
    val job: String,
    val jobDuration: Int,
    tagList: List<Tags>?,
    val isFavorite: Boolean,
    val solvedAt: LocalDateTime,
    val answer: String,
    val isAnswered: Boolean
) {
    val tagList = tagList ?: listOf()
}
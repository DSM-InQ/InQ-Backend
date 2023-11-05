package kr.hs.dsm.inq.domain.user.presentation.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import java.time.LocalDateTime

class UserQuestionDto @QueryProjection constructor (
    val questionId: Long,
    val question: String,
    val createdAt: LocalDateTime,
    val category: Category,
    tagList: List<Tags>?,
    val isAnswered: Boolean
) {
    val tagList = tagList ?: listOf()
}
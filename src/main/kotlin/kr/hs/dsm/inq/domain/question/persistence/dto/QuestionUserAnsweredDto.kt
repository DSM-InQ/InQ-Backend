package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import java.time.LocalDateTime

class QuestionUserAnsweredDto @QueryProjection constructor (
    val questionId: Long,
    val question: String,
    val category: Category,
    val username: String,
    val job: String,
    val jobDuration: Int,
    tagList: List<Tags>?,
    val isFavorite: Boolean,
    val createdAt: LocalDateTime,
    val answer: String
) {
    val tagList = tagList ?: listOf()
}
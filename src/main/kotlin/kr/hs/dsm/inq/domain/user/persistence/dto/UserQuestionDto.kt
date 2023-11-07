package kr.hs.dsm.inq.domain.user.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import java.time.LocalDateTime

class UserQuestionDto @QueryProjection constructor(
    val questionId: Long,
    val authorId: Long,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val question: String,
    val category: Category,
    tagList: List<Tags>?,
    val isFavorite: Boolean,
    val exemplaryAnswer : String,
    val createdAt: LocalDateTime
) {
    val tagList: List<Tags> = tagList ?: listOf()
}
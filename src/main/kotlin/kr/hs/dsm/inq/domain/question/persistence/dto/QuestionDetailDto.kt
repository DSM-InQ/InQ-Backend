package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import java.time.LocalDateTime
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags

class QuestionDetailDto @QueryProjection constructor(
    val questionId: Long,
    val authorId: Long,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val question: String,
    val category: Category,
    val tagList: List<Tags>,
    val isFavorite: Boolean,
    val createdAt: LocalDateTime
)
package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags

class QuestionDto @QueryProjection constructor(
    val questionId: Long,
    val question: String,
    val category: Category,
    val tagList: List<Tags>,
    val isAnswered: Boolean,
    val isFavorite: Boolean
)
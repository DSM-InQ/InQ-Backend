package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import java.time.LocalDateTime
import java.util.*

class QuestionSetDto @QueryProjection constructor(
    val questionSetId : Long?,
    val questionSetName : String?,
    val createdAt: LocalDateTime,
    val category: Category?,
    val username : String?,
    val job : String?,
    val jobDuration : Int?,
    tagList : List<Tags>?,
    val isAnswered : Boolean?,
    val likeCount : Int?,
    val viewCount : Int?,
) {
    val tagList = tagList ?: listOf()
}
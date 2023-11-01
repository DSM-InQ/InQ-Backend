package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import java.time.LocalDateTime
import kr.hs.dsm.inq.domain.question.persistence.Comments

class QuestionSetDetailDto @QueryProjection constructor(
    val questionSetId: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val category: Category,
    val likeCount: Int,
    val viewCount: Int,
    val isLiked: Boolean,
    val isDisliked: Boolean,
    val isFavorite: Boolean,
    val tagList: List<Tags>,
    commentList: List<Comments>?
) {
    val commentList: List<Comments> = commentList ?: listOf()
}

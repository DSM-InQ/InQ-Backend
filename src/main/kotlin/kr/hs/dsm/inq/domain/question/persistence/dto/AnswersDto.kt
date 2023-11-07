package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Comments

class AnswersDto @QueryProjection constructor(
    val id: Long,
    val writerId: Long,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val answer: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val dislikeCount: Int,
    val isDisliked: Boolean,
    commentList: List<Comments>?
) {
    val commentList = commentList ?: listOf()
}
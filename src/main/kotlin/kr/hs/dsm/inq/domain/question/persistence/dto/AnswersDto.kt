package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Comments

class AnswersDto @QueryProjection constructor(
    val writerId: Long,
    val username: String,
    job: String?,
    jobDuration: Int?,
    answer: String?,
    likeCount: Int?,
    isLiked: Boolean?,
    dislikeCount: Int?,
    isDisliked: Boolean?,
    commentList: List<Comments>?
) {
    val job: String = job ?: ""
    val jobDuration: Int = jobDuration ?: 1
    val answer: String = answer ?: ""
    val likeCount: Int = likeCount ?: 1
    val isLiked: Boolean = isLiked ?: true
    val dislikeCount: Int = dislikeCount ?: 1
    val isDisliked: Boolean = isDisliked ?: true
    val commentList = commentList ?: listOf()
}
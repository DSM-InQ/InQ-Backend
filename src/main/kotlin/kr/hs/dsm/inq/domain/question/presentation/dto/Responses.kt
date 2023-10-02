package kr.hs.dsm.inq.domain.question.presentation.dto

import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Comments
import kr.hs.dsm.inq.domain.question.persistence.dto.AnswersDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionDetailDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionDto

data class CreateQuestionResponses(
    val questionId: Long
)

data class QuestionListResponse(
    val hasNext: Boolean,
    val questionList: List<QuestionResponse>
) {
    companion object {
        fun of(pageResponse: PageResponse<QuestionDto>) = pageResponse.run {
            QuestionListResponse(
                hasNext = hasNext,
                questionList = list.map { QuestionResponse.of(it) }
            )
        }
    }
}

data class QuestionRankResponse(
    val hasNext: Boolean,
    val questionList: List<QuestionResponse>
) {
    companion object {
        fun of(page: Long, pageResponse: PageResponse<QuestionDto>) = pageResponse.run {
            QuestionListResponse(
                hasNext = hasNext,
                questionList = list.mapIndexed { idx, it ->
                    QuestionResponse.of(it, PageUtil.getOffset(page) + idx)
                }
            )
        }
    }
}

data class QuestionResponse(
    val questionId: Long,
    val rank: Long? = null,
    val question: String,
    val category: Category,
    val tags: List<String>,
    val isAnswered: Boolean,
    val isFavorite: Boolean
) {
    companion object {
        fun of(dto: QuestionDto) = dto.run {
            QuestionResponse(
                questionId = questionId,
                question = question,
                category = category,
                tags = tagList.map { it.tag },
                isAnswered = isAnswered,
                isFavorite = isFavorite
            )
        }

        fun of(dto: QuestionDto, rank: Long) = dto.run {
            QuestionResponse(
                questionId = questionId,
                rank = rank,
                question = question,
                category = category,
                tags = tagList.map { it.tag },
                isAnswered = isAnswered,
                isFavorite = isFavorite
            )
        }
    }
}

data class QuestionDetailResponse(
    val questionId: Long,
    val authorId: Long,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val question: String,
    val category: Category,
    val tags: List<String>,
    val isFavorite: Boolean,
    val exemplaryAnswer: AnswerResponse
) {
    companion object {
        fun of(questionDetail: QuestionDetailDto, answer: AnswersDto) = questionDetail.run {
            QuestionDetailResponse(
                questionId = questionId,
                authorId = authorId,
                username = username,
                job = job,
                jobDuration = jobDuration,
                question = question,
                category = category,
                tags = tagList.map { it.tag },
                isFavorite = isFavorite,
                exemplaryAnswer = AnswerResponse.of(answer)
            )
        }
    }
}

data class AnswerResponse(
    val username: String,
    val job: String,
    val jobDuration: Int,
    val answer: String,
    val likeCount: Int,
    val isLiked: Boolean,
    val dislikeCount: Int,
    val isDisliked: Boolean,
    val comments: List<CommentResponse>
) {
    companion object {
        fun of(answers: AnswersDto) = answers.run {
            AnswerResponse(
                username = username,
                job = job,
                jobDuration = jobDuration,
                answer = answer,
                likeCount = likeCount,
                isLiked = isLiked,
                dislikeCount = dislikeCount,
                isDisliked = isDisliked,
                comments = commentList.map { CommentResponse.of(it) }
            )
        }
    }
}

data class CommentResponse(
    val username: String,
    val job: String,
    val jobDuration: Int,
    val comment: String
) {
    companion object {
        fun of(comments: Comments) = comments.run {
            CommentResponse(
                username = writer.username,
                job = writer.job,
                jobDuration = writer.jobDuration,
                comment = comment
            )
        }
    }
}

data class TagListResponse(
    val tagList: List<String>
)

data class LikeResponse(
    val isLiked: Boolean
)

data class DislikeResponse(
    val isDisliked: Boolean
)
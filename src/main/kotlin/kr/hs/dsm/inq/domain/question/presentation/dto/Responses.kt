package kr.hs.dsm.inq.domain.question.presentation.dto

import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.dto.*
import java.time.LocalDateTime
import java.util.Date

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
                    QuestionResponse.of(it, PageUtil.getOffset(page) + idx + 1)
                }
            )
        }
    }
}

data class QuestionResponse(
    val questionId: Long,
    val rank: Long? = null,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val question: String,
    val category: Category,
    val tags: List<String>,
    val isAnswered: Boolean,
    val isFavorite: Boolean,
    val createdAt: LocalDateTime
) {
    companion object {
        fun of(dto: QuestionDto) = dto.run {
            QuestionResponse(
                questionId = questionId,
                question = question,
                category = category,
                username = username,
                job = job,
                jobDuration = jobDuration,
                tags = tagList.map { it.tag },
                isAnswered = isAnswered,
                isFavorite = isFavorite,
                createdAt = createdAt
            )
        }

        fun of(dto: QuestionDto, rank: Long) = dto.run {
            QuestionResponse(
                questionId = questionId,
                rank = rank,
                question = question,
                category = category,
                username = username,
                job = job,
                jobDuration = jobDuration,
                tags = tagList.map { it.tag },
                isAnswered = isAnswered,
                isFavorite = isFavorite,
                createdAt = createdAt
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
    val exemplaryAnswer: AnswerResponse,
    val createdAt: LocalDateTime
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
                exemplaryAnswer = AnswerResponse.of(answer),
                createdAt = createdAt
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

data class RegisterQuestionSetsResponse(
    val questionSetsName: String,
    val categories: List<CategoriesDto>,
    val likeCount: Int,
    val dislikeCount: Int,
    val isLiked: Boolean,
    val isDisliked: Boolean,
    val isFavorite: Boolean
)

data class GetQuestionSetResponse(
    val hasNext: Boolean,
    val questionSetsList: List<QuestionSet>,
) {
    companion object {
        fun of(pageResponse: PageResponse<QuestionSetDto>) = pageResponse.run {
            GetQuestionSetResponse(
                hasNext = hasNext,
                questionSetsList = list.map { QuestionSet.of(it) }
            )
        }
    }
}

data class QuestionSetRankResponse(
    val hasNext: Boolean,
    val questionSetsList: List<QuestionSet>
) {
    companion object {
        fun of(page: Long, pageResponse: PageResponse<QuestionSetDto>) = pageResponse.run {
            GetQuestionSetResponse(
                hasNext = hasNext,
                questionSetsList = list.mapIndexed { idx, it ->
                    QuestionSet.of(it, PageUtil.getOffset(page) + idx + 1)
                }
            )
        }
    }
}

data class QuestionSet(
    val questionSetId: Long?,
    val rank: Long? = null,
    val questionSetName: String?,
    val createdAt: LocalDateTime,
    val category: Category?,
    val username: String?,
    val job: String?,
    val jobDuration: Int?,
    val tags: List<String>?,
    val isAnswered: Boolean?,
    val likeCount: Int?,
    val viewCount: Int?,
) {
    companion object {
        fun of(dto: QuestionSetDto) = dto.run {
            QuestionSet(
                questionSetId = questionSetId,
                questionSetName = questionSetName,
                createdAt = createdAt,
                category = category,
                username = username,
                job = job,
                jobDuration = jobDuration,
                tags = tagList.map { it.tag },
                isAnswered = isAnswered,
                likeCount = likeCount,
                viewCount = viewCount
            )
        }

        fun of(dto: QuestionSetDto, rank: Long) = dto.run {
            QuestionSet(
                questionSetId = questionSetId,
                rank = rank,
                questionSetName = questionSetName,
                createdAt = createdAt,
                category = category,
                username = username,
                job = job,
                jobDuration = jobDuration,
                tags = tagList.map { it.tag },
                isAnswered = isAnswered,
                likeCount = likeCount,
                viewCount = viewCount
            )
        }
    }
}

data class GetQuestionSetDetailResponse(
    val questionSetId: Long,
    val name: String,
    val createdAt: LocalDateTime,
    val description: String,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val category: List<CategoriesDto?>?,
    val likeCount: Int,
    val dislikeCount: Int,
    val viewCount: Int,
    val isLiked: Boolean,
    val isDisliked: Boolean,
    val isFavorite: Boolean,
    val tags: List<String>,
    val comments: List<CommentResponse>
) {
    companion object {
        fun of(questionSetDetail: QuestionSetDetailDto, questionList: List<Questions>) = questionSetDetail.run {
            GetQuestionSetDetailResponse(
                questionSetId = questionSetId,
                name = name,
                createdAt = createdAt,
                description = description,
                username = username,
                job = job,
                jobDuration = jobDuration,
                category = questionList
                    .groupingBy { it.category }
                    .eachCount()
                    .map {
                        CategoriesDto(
                            category = it.key,
                            count = it.value
                        )
                    },
                likeCount = likeCount,
                dislikeCount = dislikeCount,
                viewCount = viewCount,
                isLiked = isLiked,
                isDisliked = isDisliked,
                isFavorite = isFavorite,
                tags = tagList.map { it.tag },
                comments = commentList.map { CommentResponse.of(it) }
            )
        }
    }
}

data class DifficultyResponse(
    val veryEasy: Int,
    val easy: Int,
    val normal: Int,
    val hard: Int,
    val veryHard: Int
)

data class favoriteResponse(
    val isFavorite: Boolean
)
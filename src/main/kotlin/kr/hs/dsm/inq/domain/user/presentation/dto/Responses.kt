package kr.hs.dsm.inq.domain.user.presentation.dto

import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.ProblemType
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetUserSolvedDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionUserSolvedDto
import java.time.LocalDate
import java.time.LocalDateTime

data class UserInfoResponse(
    val username: String,
    val joinDate: LocalDate,
    val coin: Int,
    val job: String,
    val jobDuration: Int
)

data class UserAttendanceResponse(
    val monday: Boolean,

    val tuesday: Boolean,

    val wednesday: Boolean,

    val thursday: Boolean,

    val friday: Boolean,

    val saturday: Boolean,

    val sunday: Boolean,
)

data class QuestionUserAnsweredResponse(
    val hasNext: Boolean,
    val solvedQuestionList: List<QuestionAnswered>,
    val solvedQuestionSetList: List<QuestionSetAnswered>
) {
    companion object {
        fun of(
            questionPageResponse: PageResponse<QuestionUserSolvedDto>,
            questionSetPageResponse: PageResponse<QuestionSetUserSolvedDto>
        ) =
            QuestionUserAnsweredResponse(
                hasNext = questionPageResponse.hasNext && questionSetPageResponse.hasNext,
                solvedQuestionList = questionPageResponse.list.map { QuestionAnswered.of(it) },
                solvedQuestionSetList = questionSetPageResponse.list.map { QuestionSetAnswered.of(it) }
            )

    }
}

data class QuestionAnswered(
    val type: ProblemType,
    val questionId: Long,
    val question: String,
    val category: Category,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val tags: List<String>?,
    val isFavorite: Boolean,
    val solvedAt: LocalDateTime,
    val answer: String,
    val isAnswered: Boolean
) {
    companion object {
        fun of(dto: QuestionUserSolvedDto) = dto.run {
            QuestionAnswered(
                type = problemType,
                questionId = questionId,
                question = question,
                category = category,
                username = username,
                job = job,
                jobDuration = jobDuration,
                tags = tagList.map { it.tag },
                isFavorite = isFavorite,
                solvedAt = solvedAt,
                answer = answer,
                isAnswered = isAnswered
            )
        }
    }
}

data class QuestionSetAnswered(
    val type: ProblemType,
    val questionSetId: Long,
    val questionSetName: String,
    val solvedAt: LocalDateTime
) {
    companion object {
        fun of(dto: QuestionSetUserSolvedDto) = dto.run {
            QuestionSetAnswered(
                type = problemType,
                questionSetId = questionSetId,
                questionSetName = questionSetName,
                solvedAt = solvedAt
            )
        }
    }
}
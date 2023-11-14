package kr.hs.dsm.inq.domain.user.presentation.dto

import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.ProblemType
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetDetailsUserSolved
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetUserSolvedDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionUserSolvedDto
import kr.hs.dsm.inq.domain.user.persistence.dto.UserQuestionDto

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

sealed class SolvedQuestion

data class QuestionUserAnsweredResponse(
    val hasNext: Boolean,
    val solvedQuestionList: List<SolvedQuestion>
) {
    companion object {
        fun of(
            questionPageResponse: PageResponse<QuestionUserSolvedDto>,
            questionSetPageResponse: PageResponse<QuestionSetUserSolvedDto>,
            questionSetDetailsList: List<QuestionSetDetailsUserSolved>
        ) =
            QuestionUserAnsweredResponse(
                hasNext = questionPageResponse.hasNext && questionSetPageResponse.hasNext,
                solvedQuestionList = questionPageResponse.list.map {
                    QuestionAnswered.of(it)
                } + questionSetPageResponse.list.map {
                    QuestionSetAnswered.of(
                        it,
                        questionSetDetailsList
                    )
                }
            )
    }
}

data class QuestionAnswered(
    val type: ProblemType,
    val questionId: Long,
    val question: String,
    val category: Category,
    val tags: List<String>?,
    val solvedAt: LocalDateTime,
    val answer: String,
    val isAnswered: Boolean,
    val username: String,
    val job: String,
    val jobDuration: Int
) : SolvedQuestion() {
    companion object {
        fun of(dto: QuestionUserSolvedDto) = dto.run {
            QuestionAnswered(
                type = problemType,
                questionId = questionId,
                question = question,
                category = category,
                tags = tagList.map { it.tag },
                solvedAt = solvedAt,
                answer = answer,
                isAnswered = isAnswered,
                username = username,
                job = job,
                jobDuration = jobDuration
            )
        }

        fun of(dto: QuestionSetDetailsUserSolved) = dto.run {
            QuestionAnswered(
                type = problemType,
                questionId = questionId,
                question = question,
                category = category,
                tags = tagList.map { it.tag },
                solvedAt = solvedAt,
                answer = answer,
                isAnswered = isAnswered,
                username = username,
                job = job,
                jobDuration = jobDuration
            )
        }
    }
}

data class QuestionSetAnswered(
    val type: ProblemType,
    val questionSetId: Long,
    val questionSetName: String,
    val solvedAt: LocalDateTime,
    val questionList: List<QuestionAnswered>
) : SolvedQuestion() {
    companion object {
        fun of(dto: QuestionSetUserSolvedDto, questionSetDetailsList: List<QuestionSetDetailsUserSolved>) = dto.run {
            QuestionSetAnswered(
                type = problemType,
                questionSetId = questionSetId,
                questionSetName = questionSetName,
                solvedAt = solvedAt,
                questionList = questionSetDetailsList.filter { it.questionSetId == this.questionSetId }
                    .map { QuestionAnswered.of(it) }
            )
        }
    }
}
package kr.hs.dsm.inq.domain.user.presentation.dto

import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionUserAnsweredDto
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
    val questionList: List<QuestionAnswered>
) {
    companion object {
        fun of(pageResponse: PageResponse<QuestionUserAnsweredDto>) = pageResponse.run {
            QuestionUserAnsweredResponse(
                hasNext = hasNext,
                questionList = list.map { QuestionAnswered.of(it) }
            )
        }
    }
}

data class QuestionAnswered(
    val questionId: Long,
    val question: String,
    val category: Category,
    val username: String,
    val job: String,
    val jobDuration: Int,
    val tags: List<String>?,
    val isFavorite: Boolean,
    val createdAt: LocalDateTime,
    val answer: String
) {
    companion object {
        fun of(dto: QuestionUserAnsweredDto) = dto.run {
            QuestionAnswered(
                questionId = questionId,
                question = question,
                category = category,
                username = username,
                job = job,
                jobDuration = jobDuration,
                tags = tagList.map { it.tag },
                isFavorite = isFavorite,
                createdAt = createdAt,
                answer = answer
            )
        }
    }
}
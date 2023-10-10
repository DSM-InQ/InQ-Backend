package kr.hs.dsm.inq.domain.question.presentation.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kr.hs.dsm.inq.domain.question.persistence.Category

data class CreateQuestionRequest(

    val category: Category,

    @field:NotBlank
    @field:Size(max = 200)
    val question: String,

    @field:NotBlank
    @field:Size(max = 1000)
    val answer: String,

    val tags: List<String>
)

data class AnswerRequest(
    @field:NotBlank
    @field:Size(max = 1000)
    val answer: String
)

data class GetQuestionListRequest(
    val category: Category?,
    val keyword: String?,
    val tags: List<String>?,
    val page: Long
)

data class GetQuestionRankRequest(
    val page: Long
)

data class GetPopularQuestionRequest(
    val page: Long
)
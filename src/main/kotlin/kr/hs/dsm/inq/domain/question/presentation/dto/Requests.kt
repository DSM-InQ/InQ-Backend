package kr.hs.dsm.inq.domain.question.presentation.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import org.springframework.lang.Nullable
import javax.validation.constraints.Null

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

data class QuestionSetsRequest(
    val questionSetName: String,
    val description: String,
    val category: Category,
    val questionId: List<Long>,
    val tag: List<String>,
)

data class GetQuestionSetsRequest(
    val page: Long,

    @field:Nullable
    val category: Category?,

    @field:Nullable
    val keyword: String?,

    @field:Nullable
    val tags: List<String>?
)

data class CreateCommentRequest(
    @field:NotBlank
    @field:Size(max = 1000)
    val comment: String
)
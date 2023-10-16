package kr.hs.dsm.inq.domain.question.presentation

import javax.validation.Valid
import javax.websocket.server.PathParam
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.presentation.dto.AnswerRequest
import kr.hs.dsm.inq.domain.question.presentation.dto.CreateQuestionRequest
import kr.hs.dsm.inq.domain.question.presentation.dto.CreateQuestionResponses
import kr.hs.dsm.inq.domain.question.presentation.dto.DislikeResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.GetPopularQuestionRequest
import kr.hs.dsm.inq.domain.question.presentation.dto.GetQuestionListRequest
import kr.hs.dsm.inq.domain.question.presentation.dto.GetQuestionRankRequest
import kr.hs.dsm.inq.domain.question.presentation.dto.LikeResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.QuestionDetailResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.QuestionListResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.QuestionResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.TagListResponse
import kr.hs.dsm.inq.domain.question.service.QuestionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/question")
@RestController
class QuestionController(
    private val questionService: QuestionService
) {

    @PostMapping
    fun createQuestion(@Valid @RequestBody request: CreateQuestionRequest): CreateQuestionResponses {
        return questionService.createQuestion(request)
    }

    @GetMapping
    fun getQuestionList(@Valid @ModelAttribute request: GetQuestionListRequest): QuestionListResponse {
        return questionService.getQuestionList(request)
    }

    @GetMapping("/rank")
    fun getQuestionRank(@Valid @ModelAttribute request: GetQuestionRankRequest): QuestionListResponse {
        return questionService.getQuestionRank(request)
    }

    @GetMapping("/today")
    fun getTodayQuestion(): QuestionResponse {
        return questionService.getTodayQuestion()
    }

    @GetMapping("/popular")
    fun getPopularQuestion(): QuestionListResponse {
        return questionService.getPopularQuestion()
    }

    @GetMapping("/{question-id}")
    fun getQuestionDetail(@PathVariable("question-id") questionId: Long): QuestionDetailResponse {
        return questionService.getQuestionDetail(questionId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{question-id}/answer")
    fun answerQuestion(
        @PathVariable("question-id") questionId: Long,
        @RequestBody request: AnswerRequest
    ) {
        return questionService.answerQuestion(questionId, request)
    }

    @GetMapping("/tag")
    fun getTags(@RequestParam category: Category?): TagListResponse {
        return questionService.getTagList(category)
    }

    @PostMapping("/answer/{answer-id}/like")
    fun likeAnswer(@PathVariable("answer-id") answerId: Long): LikeResponse {
        return questionService.likeAnswer(answerId)
    }

    @PostMapping("/answer/{answer-id}/dislike")
    fun dislikeAnswer(@PathVariable("answer-id") answerId: Long): DislikeResponse {
        return questionService.dislikeAnswer(answerId)
    }

}
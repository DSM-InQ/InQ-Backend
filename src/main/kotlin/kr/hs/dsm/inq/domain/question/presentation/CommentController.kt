package kr.hs.dsm.inq.domain.question.presentation

import kr.hs.dsm.inq.domain.question.presentation.dto.CreateCommentRequest
import kr.hs.dsm.inq.domain.question.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/comment")
@RestController
class CommentController(
    private val commentService: CommentService
) {

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/question-set/{question-set-id}")
    fun createQuestionSetComment(
        @PathVariable("question-set-id") questionSetId: Long,
        @RequestBody request: CreateCommentRequest
    ) {
        commentService.createComment(questionSetId, request)
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/answer/{answer-id}")
    fun createAnswerComment(
        @PathVariable("answer-id") answerId: Long,
        @RequestBody request: CreateCommentRequest
    ) {
        commentService.createComment(answerId, request)
    }
}
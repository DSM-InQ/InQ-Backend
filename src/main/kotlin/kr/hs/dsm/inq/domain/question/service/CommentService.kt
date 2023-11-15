package kr.hs.dsm.inq.domain.question.service

import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.domain.question.exception.AnswerNotFoundException
import kr.hs.dsm.inq.domain.question.exception.QuestionNotFoundException
import kr.hs.dsm.inq.domain.question.persistence.Comments
import kr.hs.dsm.inq.domain.question.persistence.repository.*
import kr.hs.dsm.inq.domain.question.presentation.dto.CreateCommentRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentsRepository: CommentsRepository,
    private val questionSetsRepository: QuestionSetsRepository,
    private val answersRepository: AnswersRepository
) {

    fun createQuestionSetComment(questionSetId: Long, request: CreateCommentRequest) {
        val user = SecurityUtil.getCurrentUser()

        val questionSets = questionSetsRepository.findByIdOrNull(questionSetId)
            ?: throw QuestionNotFoundException

        commentsRepository.save(
            Comments(
                comment = request.comment,
                writer = user,
                post = questionSets.post,
                isPrivate = request.isPrivate
            )
        )
    }

    fun createAnswerComment(answerId: Long, request: CreateCommentRequest) {
        val user = SecurityUtil.getCurrentUser()

        val answer = answersRepository.findByIdOrNull(answerId)
            ?: throw AnswerNotFoundException

        commentsRepository.save(
            Comments(
                comment = request.comment,
                writer = user,
                post = answer.post,
                isPrivate = request.isPrivate
            )
        )
    }
}
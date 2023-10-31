package kr.hs.dsm.inq.domain.question.service

import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.domain.question.exception.QuestionNotFoundException
import kr.hs.dsm.inq.domain.question.persistence.Comments
import kr.hs.dsm.inq.domain.question.persistence.repository.CommentsRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.PostRepository
import kr.hs.dsm.inq.domain.question.presentation.dto.CreateCommentRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class CommentService(
    private val commentsRepository: CommentsRepository,
    private val postRepository: PostRepository
) {

    fun createComment(postId: Long, request: CreateCommentRequest) {
        val user = SecurityUtil.getCurrentUser()

        val post = postRepository.findByIdOrNull(postId)
            ?: throw QuestionNotFoundException

        commentsRepository.save(
            Comments(
                comment = request.comment,
                writer = user,
                post = post
            )
        )
    }
}
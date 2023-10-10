package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Comments
import kr.hs.dsm.inq.domain.question.persistence.Favorite
import org.springframework.data.repository.CrudRepository

interface CommentsRepository: CrudRepository<Comments, Long> {
    fun findByPostId(postId: Long): List<Comments>
}
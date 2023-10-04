package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Like
import kr.hs.dsm.inq.domain.question.persistence.LikeId
import kr.hs.dsm.inq.domain.question.persistence.Post
import org.springframework.data.repository.CrudRepository

interface PostRepository: CrudRepository<Post, Long> {
}
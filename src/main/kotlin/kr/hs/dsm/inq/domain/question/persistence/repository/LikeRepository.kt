package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.FavoriteId
import kr.hs.dsm.inq.domain.question.persistence.Like
import kr.hs.dsm.inq.domain.question.persistence.LikeId
import org.springframework.data.repository.CrudRepository

interface LikeRepository: CrudRepository<Like, LikeId> {
}
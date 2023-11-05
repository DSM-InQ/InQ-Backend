package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Favorite
import kr.hs.dsm.inq.domain.question.persistence.FavoriteId
import org.springframework.data.repository.CrudRepository

interface FavoriteRepository: CrudRepository<Favorite, Long> {
    fun findById(id: FavoriteId): Favorite?
}
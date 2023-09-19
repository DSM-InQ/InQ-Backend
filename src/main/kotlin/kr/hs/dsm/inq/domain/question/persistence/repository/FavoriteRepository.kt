package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Favorite
import org.springframework.data.repository.CrudRepository

interface FavoriteRepository: CrudRepository<Favorite, Long> {
}
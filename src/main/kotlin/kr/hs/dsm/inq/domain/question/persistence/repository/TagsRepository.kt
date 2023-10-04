package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.CrudRepository

interface TagsRepository : JpaRepository<Tags, Long> {
    fun findByCategoryAndTagIn(category: Category, tagList: List<String>): List<Tags>
    fun findByCategory(category: Category, pageable: Pageable): List<Tags>
    fun findAllBy(pageable: Pageable): List<Tags>
}
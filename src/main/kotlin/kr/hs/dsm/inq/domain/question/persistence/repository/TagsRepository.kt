package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Tags
import org.springframework.data.repository.CrudRepository

interface TagsRepository : CrudRepository<Tags, Long> {
    fun findByCategoryAndTagIn(category: Category, tagList: List<String>): List<Tags>
    fun findTop15ByCategory(category: Category): List<Tags>
    fun findTop15(): List<Tags>
}
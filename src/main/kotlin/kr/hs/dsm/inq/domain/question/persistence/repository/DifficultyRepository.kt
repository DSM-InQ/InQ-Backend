package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Difficulty
import kr.hs.dsm.inq.domain.question.persistence.Tags
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface DifficultyRepository : JpaRepository<Difficulty, Long> {
    fun queryByQuestionsId(questionsId: Long): Difficulty?
}
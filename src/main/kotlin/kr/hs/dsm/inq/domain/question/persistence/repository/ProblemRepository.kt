package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.Problem
import kr.hs.dsm.inq.domain.question.persistence.QuestionSets
import org.springframework.data.repository.CrudRepository

interface ProblemRepository : CrudRepository<Problem, Long> {
}
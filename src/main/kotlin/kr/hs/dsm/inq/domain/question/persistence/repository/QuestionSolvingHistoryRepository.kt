package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.QuestionSolvingHistory
import org.springframework.data.repository.CrudRepository

interface QuestionSolvingHistoryRepository: CrudRepository<QuestionSolvingHistory, Long> {

}
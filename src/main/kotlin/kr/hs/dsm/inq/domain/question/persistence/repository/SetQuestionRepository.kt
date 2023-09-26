package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.SetQuestion
import kr.hs.dsm.inq.domain.question.persistence.SetQuestionID
import org.springframework.data.repository.CrudRepository

interface SetQuestionRepository : CrudRepository<SetQuestion, SetQuestionID> {
    
}
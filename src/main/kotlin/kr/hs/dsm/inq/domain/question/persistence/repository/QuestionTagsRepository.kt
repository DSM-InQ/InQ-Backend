package kr.hs.dsm.inq.domain.question.persistence.repository

import kr.hs.dsm.inq.domain.question.persistence.QuestionTags
import kr.hs.dsm.inq.domain.question.persistence.QuestionTagsId
import org.springframework.data.repository.CrudRepository

interface QuestionTagsRepository : CrudRepository<QuestionTags, QuestionTagsId> {
}
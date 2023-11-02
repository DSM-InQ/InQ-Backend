package kr.hs.dsm.inq.domain.question.persistence.dto

import com.querydsl.core.annotations.QueryProjection
import kr.hs.dsm.inq.domain.question.persistence.Category
import org.aspectj.weaver.patterns.TypePatternQuestions.Question
import java.util.Objects

class CategoriesDto @QueryProjection constructor (
    val category: Category,
    val count: Int
)
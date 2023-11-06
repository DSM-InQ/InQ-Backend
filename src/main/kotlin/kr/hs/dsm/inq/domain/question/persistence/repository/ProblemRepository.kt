package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.Problem
import kr.hs.dsm.inq.domain.question.persistence.ProblemType
import kr.hs.dsm.inq.domain.question.persistence.QFavorite
import kr.hs.dsm.inq.domain.question.persistence.QFavorite.favorite
import kr.hs.dsm.inq.domain.question.persistence.QProblem
import kr.hs.dsm.inq.domain.question.persistence.QProblem.problem
import kr.hs.dsm.inq.domain.question.persistence.QuestionSets
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetDetailDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetDto
import kr.hs.dsm.inq.domain.user.persistence.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface ProblemRepository : CrudRepository<Problem, Long>, CustomProblemRepository {
}

interface CustomProblemRepository {
    fun queryFavoriteProblem(userId: Long): List<Problem>
    fun queryFavoriteProblemSet(userId: Long): List<Problem>
}

@Repository
class CustomProblemRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): CustomProblemRepository {

    override fun queryFavoriteProblem(userId: Long): List<Problem> {
        return queryFactory
            .selectFrom(problem)
            .innerJoin(favorite).on(favorite.user.id.eq(userId))
            .where(problem.type.eq(ProblemType.QUESTION))
            .fetch()
    }

    override fun queryFavoriteProblemSet(userId: Long): List<Problem> {
        return queryFactory
            .selectFrom(problem)
            .innerJoin(favorite).on(favorite.user.id.eq(userId))
            .where(problem.type.eq(ProblemType.SET))
            .fetch()
    }

}
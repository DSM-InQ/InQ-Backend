package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.domain.question.persistence.Answers
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QComments.comments
import kr.hs.dsm.inq.domain.question.persistence.QQuestions.questions
import kr.hs.dsm.inq.domain.question.persistence.dto.AnswersDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QAnswersDto
import kr.hs.dsm.inq.domain.user.persistence.QUser.user
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface AnswersRepository: CrudRepository<Answers, Long>, CustomAnswerRepository {
}

interface CustomAnswerRepository {
    fun queryAnswerByQuestionId(questionId: Long): List<AnswersDto>
    fun queryExemplaryAnswerDto(questionId: Long, authorId: Long): AnswersDto?
}

@Repository
class CustomAnswerRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomAnswerRepository {

    override fun queryAnswerByQuestionId(questionId: Long): List<AnswersDto> {
        return queryFactory
            .selectFrom(answers)
            .where(answers.questions.id.eq(questionId))
            .getAnswerDetailDto()
    }

    override fun queryExemplaryAnswerDto(questionId: Long, authorId: Long): AnswersDto? {
        val answers = queryFactory
            .selectFrom(answers)
            .where(answers.questions.id.eq(questionId)
                .and(answers.writer.id.eq(questionId))
                .and(answers.isExamplary.isTrue)
            )
            .getAnswerDetailDto()
        return if (answers.isEmpty()) null else answers[0]
    }

    private fun <T> JPAQuery<T>.getAnswerDetailDto() =
         innerJoin(user).on(user.eq(answers.writer))
            .rightJoin(comments).on(comments.post.eq(answers.post)).fetchJoin()
            .transform(
                GroupBy.groupBy(questions)
                    .list(
                       QAnswersDto(
                           user.id,
                           user.username,
                           user.job,
                           user.jobDuration,
                           answers.answer,
                           list(comments)
                       )
                    )
            )
}
package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.domain.question.persistence.Answers
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QComments.comments
import kr.hs.dsm.inq.domain.question.persistence.QLike
import kr.hs.dsm.inq.domain.question.persistence.QPost.post
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
            .where(
                answers.questions.id.eq(questionId)
                    .and(answers.writer.id.eq(questionId))
                    .and(answers.isExamplary.isTrue)
            )
            .getAnswerDetailDto()
        return if (answers.isEmpty()) null else answers[0]
    }

    private fun <T> JPAQuery<T>.getAnswerDetailDto(): MutableList<AnswersDto> {
        val like = QLike("like")
        val dislike = QLike("dislike")
        return innerJoin(user).on(user.eq(answers.writer))
            .innerJoin(post).on(post.eq(answers.post))
            .rightJoin(like).on(like.post.eq(post).and(like.isLiked.isTrue))
            .rightJoin(dislike).on(like.post.eq(post).and(like.isLiked.isFalse))
            .rightJoin(comments).on(comments.post.eq(answers.post)).fetchJoin()
            .transform(
                GroupBy.groupBy(questions)
                    .list(
                        QAnswersDto(
                            /* writerId = */ user.id,
                            /* username = */ user.username,
                            /* job = */ user.job,
                            /* jobDuration = */ user.jobDuration,
                            /* answer = */ answers.answer,
                            /* likeCount = */ post.likeCount,
                            /* isLiked = */ like.isNotNull,
                            /* dislikeCount = */ post.dislikeCount,
                            /* isDisliked = */ dislike.isNotNull,
                            /* commentList = */ list(comments)
                       )
                    )
            )
    }
}
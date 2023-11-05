package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.domain.question.persistence.Answers
import kr.hs.dsm.inq.domain.question.persistence.QAnswers
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
    fun findByQuestionsIdAndIsExamplaryIsTrue(questionId: Long): Answers
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
            .innerJoin(questions).on(questions.id.eq(questionId))
            .getAnswerDetailDto()
    }

    override fun queryExemplaryAnswerDto(questionId: Long, authorId: Long): AnswersDto? {
        val answerResult = queryFactory
            .selectFrom(answers)
            .innerJoin(questions).on(questions.id.eq(questionId))
            .getAnswerDetailDto()
        return if (answerResult.isEmpty()) null else answerResult[0]
    }

    private fun <T> JPAQuery<T>.getAnswerDetailDto(): MutableList<AnswersDto> {
        val like = QLike("likes")
        val dislike = QLike("dislikes")
        return rightJoin(user).on(user.id.eq(answers.writer.id))
            .rightJoin(post).on(post.id.eq(answers.post.id))
            .rightJoin(like).on(like.post.id.eq(post.id).and(like.isLiked.isTrue))
            .rightJoin(dislike).on(dislike.post.id.eq(post.id).and(dislike.isLiked.isFalse))
            .rightJoin(comments).on(comments.post.eq(answers.post))
            .transform(
                GroupBy.groupBy(answers.id)
                    .list(
                        QAnswersDto(
                            /* id = */ answers.id,
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
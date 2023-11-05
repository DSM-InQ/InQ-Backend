package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.list
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QComments.comments
import kr.hs.dsm.inq.domain.question.persistence.QPost.post
import kr.hs.dsm.inq.domain.question.persistence.QQuestionTags.questionTags
import kr.hs.dsm.inq.domain.question.persistence.QQuestions.questions
import kr.hs.dsm.inq.domain.question.persistence.QTags.tags
import kr.hs.dsm.inq.domain.question.persistence.dto.AnswersDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QAnswersDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QQuestionUserAnsweredDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionUserAnsweredDto
import kr.hs.dsm.inq.domain.user.persistence.QUser
import kr.hs.dsm.inq.domain.user.persistence.QUser.user
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface AnswersRepository : CrudRepository<Answers, Long>, CustomAnswerRepository {
    fun findByQuestionsIdAndIsExamplaryIsTrue(questionId: Long): Answers
}

interface CustomAnswerRepository {
    fun queryAnswerByQuestionId(questionId: Long): List<AnswersDto>
    fun queryExemplaryAnswerDto(questionId: Long, authorId: Long): AnswersDto?
    fun queryAnswerHistoryDtoByUserId(userId: Long, page: Long): PageResponse<QuestionUserAnsweredDto>
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
                            /* writerId = */ answers.id,
                            /* username = */ answers.answer,
                            /* job = */ answers.answer,
                            /* jobDuration = */ post.likeCount,
                            /* answer = */ answers.answer,
                            /* likeCount = */ post.likeCount,
                            /* isLiked = */ post.isNotNull,
                            /* dislikeCount = */ post.dislikeCount,
                            /* isDisliked = */ post.isNotNull,
                            /* commentList = */ list(comments)
                        )
                    )
            )
    }

    override fun queryAnswerHistoryDtoByUserId(userId: Long, page: Long): PageResponse<QuestionUserAnsweredDto> {
        val writer = QUser("writer")
        val questionList = queryFactory
            .selectFrom(answers)
            .where(answers.writer.id.eq(userId))
//            .innerJoin(user).on(user.id.eq(answers.writer.id))
            .innerJoin(questions).on(questions.id.eq(answers.questions.id))
            .innerJoin(questionTags).on(questionTags.problems.eq(questions.problem))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(writer).on(writer.id.eq(questions.author.id))
            .transform(
                GroupBy.groupBy(questions)
                    .list(
                        QQuestionUserAnsweredDto(
                            questions.id,
                            questions.question,
                            questions.category,
                            writer.username,
                            writer.job,
                            writer.jobDuration,
                            GroupBy.list(tags),
                            questions.isNull,
                            questions.createdAt,
                            answers.answer
                        )
                    )
            )

        return PageUtil.toPageResponse(
            page = page,
            list = questionList
        )
    }
}
package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.core.group.GroupBy.*
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QComments.comments
import kr.hs.dsm.inq.domain.question.persistence.QPost.post
import kr.hs.dsm.inq.domain.question.persistence.QProblem.problem
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSets.questionSets
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSolvingHistory.questionSolvingHistory
import kr.hs.dsm.inq.domain.question.persistence.QQuestionTags.questionTags
import kr.hs.dsm.inq.domain.question.persistence.QQuestions.questions
import kr.hs.dsm.inq.domain.question.persistence.QSetQuestion.setQuestion
import kr.hs.dsm.inq.domain.question.persistence.QTags.tags
import kr.hs.dsm.inq.domain.question.persistence.dto.*
import kr.hs.dsm.inq.domain.user.persistence.QUser
import kr.hs.dsm.inq.domain.user.persistence.QUser.user
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface AnswersRepository : CrudRepository<Answers, Long>, CustomAnswerRepository {
    fun findByQuestionsIdAndIsExamplaryIsTrue(questionId: Long): Answers
}

interface CustomAnswerRepository {
    fun querySolvedQuestionDtoByUserId(userId: Long, page: Long): PageResponse<QuestionUserSolvedDto>
    fun querySolvedQuestionSetDtoByUserId(userId: Long, page: Long): PageResponse<QuestionSetUserSolvedDto>
    fun queryQuestionDtoByQuestionSetId(userId: Long, questionSetId: Long): List<QuestionSetDetailsUserSolved>
    fun queryAnswerByQuestionIdOrderByLikeCount(page: Long, questionId: Long): PageResponse<AnswersDto>
}

@Repository
class CustomAnswerRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomAnswerRepository {

    private fun <T> JPAQuery<T>.getAnswerDetailDto(): MutableList<AnswersDto> {
        val like = QLike("likes")
        val dislike = QLike("dislikes")
        return innerJoin(user).on(user.id.eq(answers.writer.id))
            .innerJoin(post).on(post.id.eq(answers.post.id))
            .leftJoin(like).on(like.post.id.eq(post.id).and(like.isLiked.isTrue))
            .leftJoin(dislike).on(dislike.post.id.eq(post.id).and(dislike.isLiked.isFalse))
            .leftJoin(comments).on(comments.post.id.eq(answers.post.id))
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

    override fun querySolvedQuestionDtoByUserId(userId: Long, page: Long): PageResponse<QuestionUserSolvedDto> {
        val writer = QUser("writer")
        val questionList = queryFactory
            .selectFrom(questionSolvingHistory)
            .where(questionSolvingHistory.user.id.eq(userId))
            .innerJoin(problem).on(problem.id.eq(questionSolvingHistory.problem.id))
            .innerJoin(questions).on(questions.problem.id.eq(problem.id))
            .innerJoin(answers).on(answers.questions.id.eq(questions.id).and(answers.writer.id.eq(userId)))
            .innerJoin(questionTags).on(questionTags.problems.eq(questions.problem))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(writer).on(writer.id.eq(questions.author.id))
            .transform(
                groupBy(questions)
                    .list(
                        QQuestionUserSolvedDto(
                            problem.type,
                            questions.id,
                            questions.question,
                            questions.category,
                            writer.username,
                            writer.job,
                            writer.jobDuration,
                            list(tags),
                            questions.isNull,
                            questionSolvingHistory.solvedAt,
                            answers.answer,
                            questions.isNotNull
                        )
                    )
            )

        return PageUtil.toPageResponse(
            page = page,
            list = questionList
        )
    }

    override fun querySolvedQuestionSetDtoByUserId(userId: Long, page: Long): PageResponse<QuestionSetUserSolvedDto> {
        val questionSetList = queryFactory
            .selectFrom(questionSolvingHistory)
            .where(questionSolvingHistory.user.id.eq(userId))
            .innerJoin(problem).on(problem.id.eq(questionSolvingHistory.problem.id))
            .innerJoin(questionSets).on(questionSets.problem.id.eq(problem.id))
            .transform(
                groupBy(questionSets)
                    .list(
                        QQuestionSetUserSolvedDto(
                            problem.type,
                            questionSets.id,
                            questionSets.name,
                            questionSolvingHistory.solvedAt
                        )
                    )
            )

        return PageUtil.toPageResponse(
            page = page,
            list = questionSetList
        )
    }

    override fun queryQuestionDtoByQuestionSetId(userId: Long, questionSetId: Long): List<QuestionSetDetailsUserSolved> {
        val writer = QUser("writer")
        return queryFactory
            .selectFrom(setQuestion)
            .where(setQuestion.set.id.eq(questionSetId))
            .innerJoin(questions).on(setQuestion.question.id.eq(questions.id))
            .innerJoin(problem).on(problem.id.eq(questions.problem.id))
            .innerJoin(questionTags).on(questionTags.problems.eq(questions.problem))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(writer).on(writer.id.eq(questions.author.id))
            .innerJoin(answers).on(answers.questions.id.eq(questions.id).and(answers.writer.id.eq(userId)))
            .transform(
                groupBy(questions)
                    .list(
                        QQuestionSetDetailsUserSolved(
                            problem.type,
                            setQuestion.set.id,
                            questions.id,
                            questions.question,
                            questions.category,
                            writer.username,
                            writer.job,
                            writer.jobDuration,
                            list(tags),
                            questions.isNull,
                            answers.createdAt,
                            answers.answer,
                            questions.isNotNull
                        )
                    )
            )
    }

    override fun queryAnswerByQuestionIdOrderByLikeCount(page: Long, questionId: Long): PageResponse<AnswersDto> {
        val answerList = queryFactory
            .selectFrom(answers)
            .where(answers.questions.id.eq(questionId))
            .orderBy(answers.post.likeCount.desc())
            .getAnswerDetailDto()

        return PageUtil.toPageResponse(
            page = page,
            list = answerList
        )
    }
}
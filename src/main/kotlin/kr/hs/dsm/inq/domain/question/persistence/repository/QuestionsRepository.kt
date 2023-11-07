package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSets
import kr.hs.dsm.inq.domain.question.persistence.QQuestionTags.questionTags
import kr.hs.dsm.inq.domain.question.persistence.QQuestions.questions
import kr.hs.dsm.inq.domain.question.persistence.QTags.tags
import kr.hs.dsm.inq.domain.question.persistence.Questions
import kr.hs.dsm.inq.domain.question.persistence.dto.*
import kr.hs.dsm.inq.domain.user.persistence.QUser
import kr.hs.dsm.inq.domain.user.persistence.User
import kr.hs.dsm.inq.domain.user.persistence.dto.QUserQuestionDto
import kr.hs.dsm.inq.domain.user.persistence.dto.UserQuestionDto
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface QuestionsRepository: CrudRepository<Questions, Long>, CustomQuestionRepository {
    fun findByIdIn(questionIds: List<Long>): List<Questions>
}

interface CustomQuestionRepository {

    fun queryQuestionDtoOrderByLike(
        user: User,
        page: Long,
        category: Category? = null,
        keyword: String? = "",
        tagList: List<String> = listOf(),
    ): PageResponse<QuestionDto>
    fun queryQuestionDtoOrderByAnswerCount(user: User, page: Long): PageResponse<QuestionDto>
    fun queryQuestionDtoById(id: Long, user: User): QuestionDto?
    fun queryQuestionDetailDtoById(user: User, questionId: Long): QuestionDetailDto?
    fun queryQuestionDtoByWriterId(page: Long, user: User): PageResponse<UserQuestionDto>
    fun queryQuestionDto(user: User): List<QuestionDto>
    fun queryQuestionDtoByProblemIdIn(user: User, problemIds: List<Long>): PageResponse<QuestionDto>
}

@Repository
class CustomQuestionRepositoryImpl(
    private val queryFactory: JPAQueryFactory
) : CustomQuestionRepository {

    override fun queryQuestionDtoOrderByLike(
        user: User,
        page: Long,
        category: Category?,
        keyword: String?,
        tagList: List<String>
    ): PageResponse<QuestionDto> {
        val keyword = keyword ?: ""
        val questionList = queryFactory
            .selectFrom(questions)
            .where(
                questions.question.contains(keyword)
                    .and(category?.let { questions.category.eq(it)})
            )
            .orderBy(questions.likeCount.asc())
            .getQuestionDto(user)

        return PageUtil.toPageResponse(
            page = page,
            list = questionList.filter { it.tagList.map { it.tag }.containsAll(tagList) }
        )
    }

    override fun queryQuestionDto(
        user: User,
    ): List<QuestionDto> {
        return queryFactory
            .selectFrom(questions)
            .orderBy(questions.likeCount.asc())
            .getQuestionDto(user)
    }

    override fun queryQuestionDtoByProblemIdIn(user: User, problemIds: List<Long>): PageResponse<QuestionDto> {
        val questionList = queryFactory
            .selectFrom(QQuestionSets.questionSets)
            .where(
                questions.problem.id.`in`(problemIds)
            )
            .getQuestionDto(user)

        return PageUtil.toPageResponse(
            page = 0,
            list = questionList
        )
    }

    override fun queryQuestionDtoOrderByAnswerCount(user: User, page: Long): PageResponse<QuestionDto> {
        val questionList = queryFactory
            .selectFrom(questions)
            .orderBy(questions.answerCount.asc())
            .getQuestionDto(user)

        return PageUtil.toPageResponse(
            page = page,
            list = questionList
        )
    }

    override fun queryQuestionDtoById(id: Long, user: User): QuestionDto? {
        val questions = queryFactory
            .selectFrom(questions)
            .where(questions.id.eq(id))
            .getQuestionDto(user)
        return if (questions.isEmpty()) null else questions[0]
    }

    private fun <T> JPAQuery<T>.getQuestionDto(user: User): MutableList<QuestionDto> {
        val writer = QUser("writer")
        return innerJoin(questionTags).on(questionTags.problems.eq(questions.problem))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(writer).on(writer.id.eq(questions.author.id))
//            .rightJoin(favorite).on(favorite.questions.id.eq(questions.id))
//            .rightJoin(answers).on(answers.writer.eq(user).and(answers.questions.eq(questions)))
            .transform(
                GroupBy.groupBy(questions)
                    .list(
                        QQuestionDto(
                            /* questionId = */ questions.id,
                            /* question = */ questions.question,
                            /* category = */ questions.category,
                            /* username = */ writer.username,
                            /* job = */ writer.job,
                            /* jobDuration = */ writer.jobDuration,
                            /* tagList = */ GroupBy.list(tags),
                            /* isAnswered = */ questions.isNull, // answers.isNotNull,
                            /* isFavorite = */ questions.isNull, // favorite.isNotNull
                            /* createdAt = */ questions.createdAt
                        )
                    )
            )
    }

    override fun queryQuestionDetailDtoById(
        user: User,
        questionId: Long
    ): QuestionDetailDto? {
        val questions = queryFactory
            .selectFrom(questions)
            .where(questions.id.eq(questionId))
            .getQuestionDetailDto(user)
        return if (questions.isEmpty()) null else questions[0]
    }

    private fun <T> JPAQuery<T>.getQuestionDetailDto(user: User) = run {

        val author = QUser("writer")

        return@run innerJoin(questionTags).on(questionTags.problems.eq(questions.problem))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
//            .rightJoin(favorite).on(favorite.questions.id.eq(questions.id))
//            .rightJoin(answers).on(answers.writer.eq(user).and(answers.questions.eq(questions)))
            .rightJoin(author).on(author.eq(questions.author))
            .transform(
                GroupBy.groupBy(questions)
                    .list(
                        QQuestionDetailDto(
                            /* questionId = */ questions.id,
                            /* authorId  = */ author.id,
                            /* username = */ author.username,
                            /* job = */ author.job,
                            /* jobDuration = */ author.jobDuration,
                            /* question = */ questions.question,
                            /* category = */ questions.category,
                            /* tagList = */ GroupBy.list(tags),
                            /* isFavorite = */ questions.isNull,
                            /* createdAt = */ questions.createdAt
                        )
                    )
            )
    }

    override fun queryQuestionDtoByWriterId(page: Long, user: User): PageResponse<UserQuestionDto> {
        val questions = queryFactory
            .selectFrom(questions)
            .where(questions.author.eq(user))
            .getUserQuestionListDto(user)

        return PageUtil.toPageResponse(
            page = page,
            list = questions
        )
    }

    fun <T> JPAQuery<T>.getUserQuestionListDto(user: User): List<UserQuestionDto> = run {
        val writer = QUser("writer")

        return@run innerJoin(questionTags).on(questionTags.problems.eq(questions.problem))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(answers).on(answers.questions.id.eq(questions.id))
            .rightJoin(writer).on(writer.id.eq(user.id))
            .transform(
                GroupBy.groupBy(questions)
                    .list(
                        QUserQuestionDto(
                            /* questionId = */ questions.id,
                            /* authorId = */ writer.id,
                            /* username = */ writer.username,
                            /* job = */ writer.job,
                            /* jobDuration = */ writer.jobDuration,
                            /* question = */ questions.question,
                            /* category = */ questions.category,
                            /* tagList = */ GroupBy.list(tags),
                            /* isFavorite = */ questions.isNull,
                            /* exemplaryAnswer = */ answers.answer,
                            /* createdAt = */ questions.createdAt,
                        )
                    )
            )
    }
}
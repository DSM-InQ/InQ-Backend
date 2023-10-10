package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.common.util.offsetAndLimit
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QFavorite.favorite
import kr.hs.dsm.inq.domain.question.persistence.QLike
import kr.hs.dsm.inq.domain.question.persistence.QQuestionTags.questionTags
import kr.hs.dsm.inq.domain.question.persistence.QQuestions.questions
import kr.hs.dsm.inq.domain.question.persistence.QTags.tags
import kr.hs.dsm.inq.domain.question.persistence.Questions
import kr.hs.dsm.inq.domain.question.persistence.dto.QQuestionDetailDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QQuestionDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionDetailDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionDto
import kr.hs.dsm.inq.domain.user.persistence.QUser
import kr.hs.dsm.inq.domain.user.persistence.QUser.user
import kr.hs.dsm.inq.domain.user.persistence.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface QuestionsRepository: CrudRepository<Questions, Long>, CustomQuestionRepository {
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
        val questionList = queryFactory
            .selectFrom(questions)
            .where(
                category?.let { questions.category.eq(it) },
                (keyword ?: "").let { questions.question.contains(keyword) }
            )
            .orderBy(questions.likeCount.asc())
            .offsetAndLimit(page)
            .getQuestionDto(user)
        return PageResponse(
            hasNext = PageUtil.hasNext(questionList),
            list = questionList
        )
    }

    override fun queryQuestionDtoOrderByAnswerCount(user: User, page: Long): PageResponse<QuestionDto> {
        val questionList = queryFactory
            .selectFrom(questions)
            .orderBy(questions.answerCount.asc())
            .offsetAndLimit(page)
            .getQuestionDto(user)
        return PageResponse(
            hasNext = PageUtil.hasNext(questionList),
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
        return innerJoin(questionTags).on(questionTags.questions.eq(questions))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(writer).on(writer.id.eq(questions.author.id))
            .rightJoin(favorite).on(favorite.questions.id.eq(questions.id))
            .rightJoin(answers).on(answers.writer.eq(user).and(answers.questions.eq(questions)))
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
                            /* isAnswered = */ answers.isNotNull,
                            /* isFavorite = */ favorite.isNotNull
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

        return@run innerJoin(questionTags).on(questionTags.questions.eq(questions))
            .innerJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .rightJoin(favorite).on(favorite.questions.id.eq(questions.id))
            .rightJoin(answers).on(answers.writer.eq(user).and(answers.questions.eq(questions)))
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
                            /* isFavorite = */ favorite.isNotNull
                        )
                    )
            )
    }
}
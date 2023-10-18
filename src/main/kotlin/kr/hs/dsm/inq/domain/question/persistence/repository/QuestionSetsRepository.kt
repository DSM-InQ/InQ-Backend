package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.common.util.offsetAndLimit
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSets.questionSets
import kr.hs.dsm.inq.domain.question.persistence.dto.QQuestionSetDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetDto
import kr.hs.dsm.inq.domain.user.persistence.QUser
import kr.hs.dsm.inq.domain.user.persistence.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

interface QuestionSetsRepository : CrudRepository<QuestionSets, Long>, CustomQuestionSetsRepository {
}

interface CustomQuestionSetsRepository {
    fun queryQuestionSetDto(
        user: User,
        category: Category? = null,
        keyword: String? = null,
        tags: List<String>? = null,
        page: Long
    ): PageResponse<QuestionSetDto>
}

@Repository
class CustomQuestionSetsRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): CustomQuestionSetsRepository {
    override fun queryQuestionSetDto(
        user: User,
        category: Category?,
        keyword: String?,
        tags: List<String>?,
        page: Long)
    : PageResponse<QuestionSetDto> {
        val questionSetResponse = queryFactory
            .selectFrom(questionSets)
            .where(
                questionSets.name.contains(keyword ?: "")
                    .and(category?.let { questionSets.category.eq(it)})
            )
            .offsetAndLimit(page)
            .getQuestionSetDto(user)

        return PageResponse<QuestionSetDto>(
            hasNext = PageUtil.hasNext(questionSetResponse),
            list = questionSetResponse
        )
    }

    private fun <T> JPAQuery<T>.getQuestionSetDto(user: User): MutableList<QuestionSetDto> {
        val writer = QUser("writer")
        return innerJoin(QQuestionTags.questionTags).on(QQuestionTags.questionTags.problems.eq(questionSets.problemId))
            .innerJoin(QTags.tags).on(QTags.tags.id.eq(QQuestionTags.questionTags.id.tagId))
            .innerJoin(writer).on(writer.id.eq(questionSets.authorId.id))
//            .rightJoin(favorite).on(favorite.questions.id.eq(questions.id))
//            .rightJoin(answers).on(answers.writer.eq(user).and(answers.questions.eq(questions)))
            .transform(
                GroupBy.groupBy(questionSets.id)
                    .list(
                        QQuestionSetDto(
                            /* questionSetId = */ questionSets.id,
                            /* questionSetName = */ questionSets.name,
                            /* createdAt = */ questionSets.createdAt,
                            /* category = */ questionSets.category,
                            /* username = */ writer.username,
                            /* job = */ writer.job,
                            /* jobDuration = */ writer.jobDuration,
                            /* tags = */ GroupBy.list(QTags.tags),
                            /* isAnswered = */ questionSets.isNull(),
                            /* likeCount = */ questionSets.likeCount,
                            /* viewCount = */ questionSets.viewCount,
                        )
                    )
            )
    }
}


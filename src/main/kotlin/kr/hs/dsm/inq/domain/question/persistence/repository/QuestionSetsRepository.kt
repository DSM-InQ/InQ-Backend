package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QComments.comments
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSets.questionSets
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSolvingHistory.questionSolvingHistory
import kr.hs.dsm.inq.domain.question.persistence.QQuestionTags.questionTags
import kr.hs.dsm.inq.domain.question.persistence.QTags.tags
import kr.hs.dsm.inq.domain.question.persistence.dto.QAnswersDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QQuestionSetDetailDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QQuestionSetDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetDetailDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetDto
import kr.hs.dsm.inq.domain.user.persistence.QUser
import kr.hs.dsm.inq.domain.user.persistence.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

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

    fun queryQuestionSetDtoById(
        user: User,
        id: Long
    ): QuestionSetDetailDto?
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
        page: Long
    ): PageResponse<QuestionSetDto> {
        val questionSetResponse = queryFactory
            .selectFrom(questionSets)
            .where(
                questionSets.name.contains(keyword ?: "")
                    .and(category?.let { questionSets.category.eq(it)})
            )
            .getQuestionSetDto(user)

        return PageUtil.toPageResponse(
            page = page,
            list = questionSetResponse
        )
    }

    @Transactional
    override fun queryQuestionSetDtoById(
        user: User,
        id: Long
    ): QuestionSetDetailDto? {
        queryFactory
            .update(questionSets)
            .set(questionSets.viewCount, questionSets.viewCount.add(1))
            .where(questionSets.id.eq(id))
            .execute()

        val questionSetDetailResponse = queryFactory
            .selectFrom(questionSets)
            .where(questionSets.id.eq(id))
            .getQuestionSetDetailDto(user)

        return if (questionSetDetailResponse.isEmpty()) null else questionSetDetailResponse[0]
    }

    private fun <T> JPAQuery<T>.getQuestionSetDto(user: User): MutableList<QuestionSetDto> {
        val author = QUser("writer")
        return leftJoin(questionTags).on(questionTags.problems.eq(questionSets.problemId))
            .leftJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .leftJoin(questionSolvingHistory)
                .on(questionSolvingHistory.userId.id.eq(user.id)).on(questionSolvingHistory.problem.eq(questionSets.problemId))
            .innerJoin(author).on(author.id.eq(questionSets.authorId.id))
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
                            /* username = */ author.username,
                            /* job = */ author.job,
                            /* jobDuration = */ author.jobDuration,
                            /* tagList = */ GroupBy.list(tags),
                            /* isAnswered = */ questionSolvingHistory.isNotNull,
                            /* likeCount = */ questionSets.likeCount,
                            /* viewCount = */ questionSets.viewCount,
                        )
                    )
            )
    }

    private fun <T> JPAQuery<T>.getQuestionSetDetailDto(user: User) = run {

        val author = QUser("writer")
        val liked = QLike("liked")
        val favorite = QFavorite("favorite")
        return@run leftJoin(questionTags).on(questionTags.problems.eq(questionSets.problemId))
            .leftJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(author).on(author.id.eq(questionSets.author.id))
            .leftJoin(liked).on(liked.id.userId.eq(user.id)).on(liked.post.eq(questionSets.post))
            .leftJoin(favorite).on(favorite.id.userId.eq(user.id)).on(favorite.problemId.eq(questionSets.problem))
            .rightJoin(comments).on(comments.post.eq(questionSets.post))
            .transform(
                GroupBy.groupBy(questionSets)
                    .list(
                        QQuestionSetDetailDto(
                            /* questionSetId = */ questionSets.id,
                            /* name = */ questionSets.name,
                            /* createdAt = */ questionSets.createdAt,
                            /* username = */ author.username,
                            /* job = */ author.job,
                            /* jobDuration = */ author.jobDuration,
                            /* category = */ questionSets.category,
                            /* likeCount = */ questionSets.likeCount,
                            /* viewCount = */ questionSets.viewCount,
                            /* isLiked = */ liked.isLiked.isTrue,
                            /* isDisliked = */ liked.isLiked.isFalse,
                            /* isFavorite = */ favorite.isNotNull,
                            /* tagList = */ GroupBy.list(tags),
                            /* comments = */ GroupBy.list(comments)
                        )
                    )
            )
    }
}
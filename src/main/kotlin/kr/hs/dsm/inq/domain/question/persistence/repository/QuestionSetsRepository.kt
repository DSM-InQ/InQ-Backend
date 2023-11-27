package kr.hs.dsm.inq.domain.question.persistence.repository

import com.querydsl.core.group.GroupBy
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.QAnswers.answers
import kr.hs.dsm.inq.domain.question.persistence.QComments.comments
import kr.hs.dsm.inq.domain.question.persistence.QFavorite.favorite
import kr.hs.dsm.inq.domain.question.persistence.QPost.post
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSets.questionSets
import kr.hs.dsm.inq.domain.question.persistence.QQuestionSolvingHistory.questionSolvingHistory
import kr.hs.dsm.inq.domain.question.persistence.QQuestionTags.questionTags
import kr.hs.dsm.inq.domain.question.persistence.QQuestions.questions
import kr.hs.dsm.inq.domain.question.persistence.QTags.tags
import kr.hs.dsm.inq.domain.question.persistence.dto.*
import kr.hs.dsm.inq.domain.user.persistence.QUser
import kr.hs.dsm.inq.domain.user.persistence.User
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

interface QuestionSetsRepository : CrudRepository<QuestionSets, Long>, CustomQuestionSetsRepository {
}

interface CustomQuestionSetsRepository {
    fun queryQuestionSetDtoOrderByLike(
        user: User,
        category: Category? = null,
        keyword: String? = null,
        tags: List<String>? = null,
        page: Long
    ): PageResponse<QuestionSetDto>

    fun queryQuestionSetDetailDtoById(
        user: User,
        id: Long
    ): QuestionSetDetailDto?

    fun queryQuestionSetDtoByProblemIdIn(user: User, problemIds: List<Long>): PageResponse<QuestionSetDto>

    fun queryQuestionSetDtoByWriter(page: Int, user: User): PageResponse<QuestionSetDto>
}

@Repository
class CustomQuestionSetsRepositoryImpl(
    private val queryFactory: JPAQueryFactory
): CustomQuestionSetsRepository {
    override fun queryQuestionSetDtoOrderByLike(
        user: User,
        category: Category?,
        keyword: String?,
        tags: List<String>?,
        page: Long
    ): PageResponse<QuestionSetDto> {
        val questionSetList = queryFactory
            .selectFrom(questionSets)
            .where(
                questionSets.name.contains(keyword ?: "")
                    .and(category?.let { questionSets.category.eq(it)})
            )
            .orderBy(questionSets.post.likeCount.desc())
            .getQuestionSetDto(user)

        return PageUtil.toPageResponse(
            page = page,
            list = questionSetList
        )
    }

    @Transactional
    override fun queryQuestionSetDetailDtoById(
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

    override fun queryQuestionSetDtoByProblemIdIn(user: User, problemIds: List<Long>): PageResponse<QuestionSetDto> {
        val questionSetList = queryFactory
            .selectFrom(questionSets)
            .where(
                questionSets.problem.id.`in`(problemIds)
            )
            .orderBy(questionSets.post.likeCount.asc())
            .getQuestionSetDto(user)

        return PageUtil.toPageResponse(
            page = 0,
            list = questionSetList
        )
    }

    private fun <T> JPAQuery<T>.getQuestionSetDto(user: User): List<QuestionSetDto> {
        val author = QUser("writer")
        return leftJoin(questionTags).on(questionTags.problems.eq(questionSets.problem))
            .leftJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .leftJoin(questionSolvingHistory)
                .on(questionSolvingHistory.user.id.eq(user.id)).on(questionSolvingHistory.problem.eq(questionSets.problem))
            .innerJoin(author).on(author.id.eq(questionSets.author.id))
            .innerJoin(post).on(post.id.eq(questionSets.post.id))
            .leftJoin(favorite).on(favorite.problem.id.eq(questionSets.problem.id).and(favorite.user.id.eq(user.id)))
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
                            /* isFavorite = */ favorite.isNotNull,
                            /* likeCount = */ post.likeCount,
                            /* viewCount = */ questionSets.viewCount,
                        )
                    )
            )
    }

    private fun <T> JPAQuery<T>.getQuestionSetDetailDto(user: User) = run {

        val author = QUser("writer")
        val liked = QLike("liked")
        val favorite = QFavorite("favorite")

        return@run leftJoin(questionTags).on(questionTags.problems.eq(questionSets.problem))
            .leftJoin(tags).on(tags.id.eq(questionTags.id.tagId))
            .innerJoin(author).on(author.id.eq(questionSets.author.id))
            .leftJoin(liked).on(liked.id.userId.eq(user.id)).on(liked.post.eq(questionSets.post))
            .leftJoin(post).on(post.id.eq(questionSets.post.id))
            .leftJoin(favorite).on(favorite.id.userId.eq(user.id)).on(favorite.problem.eq(questionSets.problem))
            .leftJoin(comments).on(comments.post.eq(questionSets.post))
            .transform(
                GroupBy.groupBy(questionSets)
                    .list(
                        QQuestionSetDetailDto(
                            /* questionSetId = */ questionSets.id,
                            /* name = */ questionSets.name,
                            /* createdAt = */ questionSets.createdAt,
                            /* description = */ questionSets.description,
                            /* writerId = */ author.id,
                            /* username = */ author.username,
                            /* job = */ author.job,
                            /* jobDuration = */ author.jobDuration,
                            /* likeCount = */ post.likeCount,
                            /* dislikeCount = */ post.dislikeCount,
                            /* viewCount = */ questionSets.viewCount,
                            /* isLiked = */ liked.isLiked.isTrue,
                            /* isDisliked = */ liked.isLiked.isFalse,
                            /* isFavorite = */ favorite.isNotNull,
                            /* tagList = */ GroupBy.list(tags),
                            /* commentList = */ GroupBy.list(comments)
                        )
                    )
            )
    }

    override fun queryQuestionSetDtoByWriter(page: Int, user: User): PageResponse<QuestionSetDto> {
        val questionSets = queryFactory
            .selectFrom(questionSets)
            .where(questionSets.author.eq(user))
            .getQuestionSetDto(user)

        return PageUtil.toPageResponse(
            page = page.toLong(),
            list = questionSets
        )
    }
}

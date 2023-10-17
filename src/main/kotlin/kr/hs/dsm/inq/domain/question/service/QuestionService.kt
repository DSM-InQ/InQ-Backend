package kr.hs.dsm.inq.domain.question.service

import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.common.util.defaultPage
import kr.hs.dsm.inq.domain.question.exception.AlreadyDislikedPostException
import kr.hs.dsm.inq.domain.question.exception.AlreadyLikedPostException
import kr.hs.dsm.inq.domain.question.exception.AnswerNotFoundException
import kr.hs.dsm.inq.domain.question.exception.QuestionNotFoundException
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.dto.AnswersDto
import kr.hs.dsm.inq.domain.question.persistence.repository.AnswersRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.CommentsRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.LikeRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.PostRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.ProblemRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.QuestionTagsRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.QuestionsRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.QuestionSetsRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.SetQuestionRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.TagsRepository
import kr.hs.dsm.inq.domain.question.presentation.dto.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Transactional
@Service
class QuestionService(
    private val questionsRepository: QuestionsRepository,
    private val answersRepository: AnswersRepository,
    private val tagsRepository: TagsRepository,
    private val questionTagsRepository: QuestionTagsRepository,
    private val commentRepository: CommentsRepository,
    private val problemRepository: ProblemRepository,
    private val likeRepository: LikeRepository,
    private val postRepository: PostRepository,
    private val questionSetsRepository: QuestionSetsRepository,
    private val setQuestionRepository: SetQuestionRepository,
) {

    fun createQuestion(request: CreateQuestionRequest): CreateQuestionResponses {

        val user = SecurityUtil.getCurrentUser()

        val problem = problemRepository.save(
            Problem(type = ProblemType.QUESTION)
        )

        val questions = questionsRepository.save(
            Questions(
                question = request.question,
                category = request.category,
                author = user,
                problem = problem
            )
        )

        saveTag(
            request = request,
            questions = questions
        )

        val post = postRepository.save(Post())

        answersRepository.save(
            Answers(
                isExamplary = true,
                answer = request.answer,
                questions = questions,
                writer = user,
                post = post
            )
        )

        return CreateQuestionResponses(
            questionId = questions.id
        )
    }

    private fun saveTag(
        request: CreateQuestionRequest,
        questions: Questions
    ) {
        val existsTagList = tagsRepository.findByCategoryAndTagIn(request.category, request.tags)
        val existsTagMap = existsTagList.associateBy { it.tag }
        val tagListToSave = request.tags
            .mapNotNull { if (existsTagMap[it] != null) null else it }

        val tagList = tagsRepository.saveAll(
            tagListToSave.map {
                Tags(
                    tag = it,
                    category = request.category
                )
            }
        ).union(existsTagList)

        questionTagsRepository.saveAll(
            tagList.map {
                QuestionTags(
                    id = QuestionTagsId(questionId = questions.id, tagId = it.id),
                    questions = questions,
                    tags = it
                )
            }
        )
    }

    fun getQuestionList(request: GetQuestionListRequest): QuestionListResponse {

        val user = SecurityUtil.getCurrentUser()

        val questionList = request.run {
            questionsRepository.queryQuestionDtoOrderByLike(
                user = user,
                page = page,
                category = category,
                keyword = keyword,
                tagList = tags ?: listOf()
            )
        }

        return QuestionListResponse.of(questionList)
    }

    fun getQuestionRank(request: GetQuestionRankRequest): QuestionListResponse {

        val user = SecurityUtil.getCurrentUser()
        val questionList = questionsRepository.queryQuestionDtoOrderByLike(
            user = user,
            page = request.page
        )

        return QuestionRankResponse.of(
            page = request.page,
            pageResponse = questionList
        )
    }

    fun getTodayQuestion(): QuestionResponse {
        val user = SecurityUtil.getCurrentUser()
        val todayQuestion = questionsRepository.queryQuestionDtoById(1L, user) ?: throw QuestionNotFoundException
        return QuestionResponse.of(todayQuestion)
    }

    fun getPopularQuestion(): QuestionListResponse {

        val user = SecurityUtil.getCurrentUser()
        val questionList = questionsRepository.queryQuestionDtoOrderByAnswerCount(
            user = user,
            page = 1L
        )

        return QuestionListResponse.of(questionList)
    }

    fun getQuestionDetail(questionId: Long): QuestionDetailResponse {

        val user = SecurityUtil.getCurrentUser()
        val question = questionsRepository.queryQuestionDetailDtoById(
            user = user,
            questionId = questionId
        ) ?: throw QuestionNotFoundException

        val exemplaryAnswer = answersRepository.findByQuestionsIdAndIsExamplaryIsTrue(questionId)
        val post = exemplaryAnswer.post
        val like = likeRepository.findByPostIdAndUserId(post.id, user.id)
        val comments = commentRepository.findByPostId(post.id)

        return QuestionDetailResponse.of(
            questionDetail = question,
            answer = AnswersDto(
                writerId = question.authorId,
                username = question.username,
                job = question.job,
                jobDuration = question.jobDuration,
                answer = exemplaryAnswer.answer,
                likeCount = post.likeCount,
                isLiked = like?.isLiked == true,
                dislikeCount = post.dislikeCount,
                isDisliked = like?.isLiked == false,
                commentList = comments
            )
        )
    }

    fun getTagList(category: Category?): TagListResponse {

        val tagList = category?.let {
            tagsRepository.findByCategory(it, defaultPage)
        } ?: tagsRepository.findAllBy(defaultPage)

        return TagListResponse(
            tagList = tagList.map { it.tag }
        )
    }

    fun answerQuestion(questionId: Long, request: AnswerRequest) {

        val user = SecurityUtil.getCurrentUser()

        val questions = questionsRepository.findByIdOrNull(questionId) ?: throw QuestionNotFoundException
        
        val post = postRepository.save(Post())

        answersRepository.save(
            Answers(
                isExamplary = true,
                answer = request.answer,
                questions = questions,
                writer = user,
                post = post
            )
        )

        questionsRepository.save(
            questions.apply { answerCount += 1 }
        )
    }

    fun likeAnswer(answerId: Long): LikeResponse {

        val user = SecurityUtil.getCurrentUser()
        val answer = answersRepository.findByIdOrNull(answerId) ?: throw AnswerNotFoundException

        val likeId = LikeId(answer.post.id, user.id)
        val like = likeRepository.findByIdOrNull(likeId)

        if (like == null) {
            postRepository.save(
                answer.post.apply { addLikeCount() }
            )
            likeRepository.save(
                Like(
                    id = likeId,
                    post = answer.post,
                    user = user,
                    isLiked = true
                )
            )
            return LikeResponse(isLiked = true)
        } else if (!like.isLiked) {
            throw AlreadyDislikedPostException
        } else {
            postRepository.save(
                answer.post.apply { reduceLikeCount() }
            )
            likeRepository.deleteById(likeId)
            return LikeResponse(isLiked = false)
        }
    }

    fun dislikeAnswer(answerId: Long): DislikeResponse {

        val user = SecurityUtil.getCurrentUser()
        val answer = answersRepository.findByIdOrNull(answerId) ?: throw AnswerNotFoundException

        val likeId = LikeId(answer.post.id, user.id)
        val like = likeRepository.findByIdOrNull(likeId)

        if (like == null) {
            postRepository.save(
                answer.post.apply { addDislikeCount() }
            )
            likeRepository.save(
                Like(
                    id = likeId,
                    post = answer.post,
                    user = user,
                    isLiked = true
                )
            )
            return DislikeResponse(isDisliked = true)
        } else if (like.isLiked) {
            throw AlreadyLikedPostException
        } else {
            postRepository.save(
                answer.post.apply { reduceDislikeCount() }
            )
            likeRepository.deleteById(likeId)
            return DislikeResponse(isDisliked = false)
        }
    }

    fun registerQuestionSet(request: QuestionSetsRequest): QuestionSetsResponse{
        val postId = postRepository.save(Post())
        val problemId = problemRepository.save(Problem(type = ProblemType.SET))

        val sets = questionSetsRepository.save(
            QuestionSets(
                name = request.questionSetName,
                answerCount = 0,
                postId = postId,
                problemId = problemId,
            )
        )

        request.tag.map{
            tagsRepository.save(
                Tags(
                    tag = it,
                    category = null,
                )
            )
        }

        val len = request.questionId.size - 1
        val categories: MutableList<categories> = mutableListOf()
        for(i: Int in 0..len) {
            val questionId = request.questionId[i]

            val question = questionsRepository.findByIdOrNull(questionId) ?: throw QuestionNotFoundException

            setQuestionRepository.save(
                SetQuestion(
                    id = SetQuestionID(
                        setId = sets.id,
                        questionId = questionId,
                    ),
                    setId = sets,
                    questionId = question,
                    question_index = i + 1,
                )
            )

            categories.add(categories(count = 1, category = question.category))

        }

        val count = categories.groupingBy { it }.eachCount()
        print(count)

        return QuestionSetsResponse(
            questionSetsName = request.questionSetName,
            categories = categories,
            likeCount = 0,
            dislikeCount = 0,
            isLiked = false,
            isDisliked = false,
            isFavorite = false
        )
    }
}
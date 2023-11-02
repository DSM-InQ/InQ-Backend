package kr.hs.dsm.inq.domain.question.service

import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.common.util.defaultPage
import kr.hs.dsm.inq.domain.question.exception.AlreadyDislikedPostException
import kr.hs.dsm.inq.domain.question.exception.AlreadyLikedPostException
import kr.hs.dsm.inq.domain.question.exception.AnswerNotFoundException
import kr.hs.dsm.inq.domain.question.exception.QuestionNotFoundException
import kr.hs.dsm.inq.domain.question.exception.QuestionSetNotFoundException
import kr.hs.dsm.inq.domain.question.exception.*
import kr.hs.dsm.inq.domain.question.persistence.*
import kr.hs.dsm.inq.domain.question.persistence.dto.AnswersDto
import kr.hs.dsm.inq.domain.question.persistence.dto.CategoriesDto
import kr.hs.dsm.inq.domain.question.persistence.repository.*
import kr.hs.dsm.inq.domain.question.presentation.dto.*
import kr.hs.dsm.inq.domain.user.persistence.User
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
    private val questionSolvingHistoryRepository: QuestionSolvingHistoryRepository,
    private val difficultyRepository: DifficultyRepository
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
            category = request.category,
            tags = request.tags,
            problems = problem,
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
        category: Category,
        tags: List<String>,
        problems: Problem,
    ) {
        val existsTagList = tagsRepository.findByCategoryAndTagIn(category, tags)
        val existsTagMap = existsTagList.associateBy { it.tag }
        val tagListToSave = tags
            .mapNotNull { if (existsTagMap[it] != null) null else it }

        val tagList = tagsRepository.saveAll(
            tagListToSave.map {
                Tags(
                    tag = it,
                    category = category
                )
            }
        ).union(existsTagList)

        questionTagsRepository.saveAll(
            tagList.map {
                QuestionTags(
                    id = QuestionTagsId(problemId = problems.id, tagId = it.id),
                    problems = problems,
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

        questionSolvingHistoryRepository.save(
            QuestionSolvingHistory(
                userId = user,
                problem = questions.problem
            )
        )
    }

    fun likeAnswer(answerId: Long): LikeResponse {
        val user = SecurityUtil.getCurrentUser()
        val answer = answersRepository.findByIdOrNull(answerId) ?: throw AnswerNotFoundException
        return toggleLike(answer.post, user)
    }

    fun likeQuestionSet(questionSetId: Long): LikeResponse {
        val user = SecurityUtil.getCurrentUser()
        val questionSet = questionSetsRepository.findByIdOrNull(questionSetId) ?: throw QuestionSetNotFoundException
        return toggleLike(questionSet.post, user)
    }

    private fun toggleLike(
        post: Post,
        user: User
    ): LikeResponse {
        val likeId = LikeId(post.id, user.id)
        val like = likeRepository.findByIdOrNull(likeId)
        return if (like == null) {
            postRepository.save(
                post.apply { addLikeCount() }
            )
            likeRepository.save(
                Like(
                    id = likeId,
                    post = post,
                    user = user,
                    isLiked = true
                )
            )
            LikeResponse(isLiked = true)
        } else if (!like.isLiked) {
            throw AlreadyDislikedPostException
        } else {
            postRepository.save(
                post.apply { reduceLikeCount() }
            )
            likeRepository.deleteById(likeId)
            LikeResponse(isLiked = false)
        }
    }

    fun dislikeAnswer(answerId: Long): DislikeResponse {
        val user = SecurityUtil.getCurrentUser()
        val answer = answersRepository.findByIdOrNull(answerId) ?: throw AnswerNotFoundException
        return toggleDislike(answer.post, user)
    }

    fun dislikeQuestionSet(questionSetId: Long): DislikeResponse {
        val user = SecurityUtil.getCurrentUser()
        val questionSet = questionSetsRepository.findByIdOrNull(questionSetId) ?: throw QuestionSetNotFoundException
        return toggleDislike(questionSet.post, user)
    }

    private fun toggleDislike(
        post: Post,
        user: User
    ): DislikeResponse {
        val likeId = LikeId(post.id, user.id)
        val like = likeRepository.findByIdOrNull(likeId)
        return if (like == null) {
            // doDislike
            postRepository.save(
                post.apply { addDislikeCount() }
            )
            likeRepository.save(
                Like(
                    id = likeId,
                    post = post,
                    user = user,
                    isLiked = true
                )
            )
            DislikeResponse(isDisliked = true)
        } else if (like.isLiked) {
            throw AlreadyLikedPostException
        } else {
            // cancelDislike
            postRepository.save(
                post.apply { reduceDislikeCount() }
            )
            likeRepository.deleteById(likeId)
            DislikeResponse(isDisliked = false)
        }
    }

    fun registerQuestionSet(request: QuestionSetsRequest): RegisterQuestionSetsResponse{
        val user = SecurityUtil.getCurrentUser()

        val post = postRepository.save(Post())
        val problem = problemRepository.save(Problem(type = ProblemType.SET))

        val sets = questionSetsRepository.save(
            QuestionSets(
                name = request.questionSetName,
                answerCount = 0,
                description = request.description,
                category = request.category,
                likeCount = 0,
                dislikeCount = 0,
                viewCount = 0,
                post = post,
                problem = problem,
                author = user,
            )
        )

        saveTag(
            category = request.category,
            tags = request.tag,
            problems = sets.problem
        )

        val questions = questionsRepository.findByIdIn(request.questionId)

        questions.forEachIndexed {
            index, question -> run {
                setQuestionRepository.save(
                    SetQuestion(
                        id = SetQuestionId(
                            setId = sets.id,
                            questionId = question.id,
                        ),
                        set = sets,
                        question = question,
                        questionIndex = index,
                    )
                )

            }
        }

        val categoryCount = questions
            .groupingBy { it.category }
            .eachCount()
            .map {
                CategoriesDto(
                    category = it.key,
                    count = it.value
                )
            }

        return RegisterQuestionSetsResponse(
            questionSetsName = request.questionSetName,
            categories = categoryCount,
            likeCount = 0,
            dislikeCount = 0,
            isLiked = false,
            isDisliked = false,
            isFavorite = false
        )
    }

    fun getQuestionSet(request: GetQuestionSetsRequest): GetQuestionSetResponse {
        val user = SecurityUtil.getCurrentUser()

        val questionSetList = request.run{
            questionSetsRepository.queryQuestionSetDto(
                user = user,
                category = category,
                keyword = keyword,
                tags = tags ?: listOf(),
                page = page,
            )
        }

        return GetQuestionSetResponse.of(questionSetList)
    }

    fun getQuestionSetDetail(questionSetId: Long): GetQuestionSetDetailResponse {

        val user = SecurityUtil.getCurrentUser()

        val questionSetDetail = questionSetId.run {
            questionSetsRepository.queryQuestionSetDetailDtoById(user, questionSetId)
                ?: throw QuestionSetNotFoundException
        }

        val setQuestionList = setQuestionRepository.findAllBySetId(questionSetDetail.questionSetId)
        val questionList = questionsRepository.findByIdIn(setQuestionList.map { it.question.id })

        return GetQuestionSetDetailResponse.of(questionSetDetail, questionList)
    }

    fun answerQuestionSet(questionSetId: Long){
        val user = SecurityUtil.getCurrentUser()

        val questionSet = questionSetsRepository.findByIdOrNull(questionSetId)?: throw QuestionSetNotFoundException

        questionSolvingHistoryRepository.save(
            QuestionSolvingHistory(
                userId = user,
                problem = questionSet.problem
            )
        )
    }

    fun answerQuestionInQuestionSet(
        questionId: Long,
        request: AnswerRequest
    ){
        val user = SecurityUtil.getCurrentUser()

        val question = questionsRepository.findByIdOrNull(questionId)?: throw QuestionNotFoundException

        val post = postRepository.save(Post())

        answersRepository.save(
            Answers(
                isExamplary = false,
                answer = request.answer,
                questions = question,
                writer = user,
                post = post,
            )
        )

        questionsRepository.save(
            question.apply { answerCount += 1 }
        )
    }

    fun assessDifficulty(questionId: Long, difficultyLevel: DifficultyLevel): DifficultyResponse {

        val questions = questionsRepository.findByIdOrNull(questionId) ?: throw QuestionNotFoundException

        val difficulty = difficultyRepository.queryByQuestionsId(questions.id) ?: difficultyRepository.save(
            Difficulty(questions = questions)
        )

        return DifficultyResponse(
            veryEasy = difficulty.getPercentage(DifficultyLevel.VERY_EASY),
            easy = difficulty.getPercentage(DifficultyLevel.EASY),
            normal = difficulty.getPercentage(DifficultyLevel.NORMAL),
            hard = difficulty.getPercentage(DifficultyLevel.HARD),
            veryHard = difficulty.getPercentage(DifficultyLevel.VERY_HARD),
        )
    }
}
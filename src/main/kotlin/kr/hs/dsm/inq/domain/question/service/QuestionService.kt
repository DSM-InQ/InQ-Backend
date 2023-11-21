package kr.hs.dsm.inq.domain.question.service

import kr.hs.dsm.inq.common.util.PageUtil
import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.common.util.defaultPage
import kr.hs.dsm.inq.domain.question.exception.AlreadyDislikedPostException
import kr.hs.dsm.inq.domain.question.exception.AlreadyLikedPostException
import kr.hs.dsm.inq.domain.question.exception.AnswerNotFoundException
import kr.hs.dsm.inq.domain.question.exception.QuestionNotFoundException
import kr.hs.dsm.inq.domain.question.exception.QuestionSetNotFoundException
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
    private val difficultyRepository: DifficultyRepository,
    private val favoriteRepository: FavoriteRepository,
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
        val todayQuestion = questionsRepository.queryQuestionDto(user = user).get(0)
        return QuestionResponse.of(todayQuestion)
    }

    fun getRandomQuestion(category: Category?): QuestionResponse {
        val user = SecurityUtil.getCurrentUser()
        val random = questionsRepository.queryQuestionDtoOrderByLike(
            user = user,
            page = 0,
            category = category
        ).list.random()
        return QuestionResponse.of(random)
    }

    fun getPopularQuestion(): QuestionListResponse {
        val user = SecurityUtil.getCurrentUser()
        val questionList = questionsRepository.queryQuestionDtoOrderByAnswerCount(
            user = user,
            page = 1L
        )
        return QuestionListResponse.of(questionList)
    }

    fun getPopularQuestionSet(): QuestionSetListResponse {
        val user = SecurityUtil.getCurrentUser()
        val questionSetList = questionSetsRepository.queryQuestionSetDtoOrderByLike(
            user = user,
            category = null,
            keyword = "",
            tags = listOf(),
            page = 1L
        )
        return QuestionSetListResponse.of(questionSetList)
    }

    fun getFavoriteQuestion(): QuestionListResponse {
        val user = SecurityUtil.getCurrentUser()
        val problems = problemRepository.queryFavoriteProblem(user.id)
        val questionList = questionsRepository.queryQuestionDtoByProblemIdIn(user, problems.map { it.id })
        return QuestionListResponse.of(questionList)
    }

    fun getFavoriteQuestionSet(): QuestionSetListResponse {
        val user = SecurityUtil.getCurrentUser()
        val problems = problemRepository.queryFavoriteProblemSet(user.id)
        val questionSetList = questionSetsRepository.queryQuestionSetDtoByProblemIdIn(
            user = user,
            problemIds = problems.map { it.id }
        )
       return QuestionSetListResponse.of(questionSetList)
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
                id = exemplaryAnswer.id,
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
            ),
            user = user
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
                isExamplary = false,
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
                user = user,
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
            post.apply {
                addLikeCount()
                postRepository.save(this)
            }
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
            post.apply {
                reduceLikeCount()
                postRepository.save(this)
            }
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
            post.apply {
                addDislikeCount()
                postRepository.save(this)
            }
            likeRepository.save(
                Like(
                    id = likeId,
                    post = post,
                    user = user,
                    isLiked = false
                )
            )
            DislikeResponse(isDisliked = true)
        } else if (like.isLiked) {
            throw AlreadyLikedPostException
        } else {
            post.apply {
                reduceDislikeCount()
                postRepository.save(this)
            }
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

    fun getQuestionSet(request: GetQuestionSetsRequest): QuestionSetListResponse {
        val user = SecurityUtil.getCurrentUser()

        val questionSetList = request.run{
            questionSetsRepository.queryQuestionSetDtoOrderByLike(
                user = user,
                category = category,
                keyword = keyword,
                tags = tags ?: listOf(),
                page = page,
            )
        }

        return QuestionSetListResponse.of(questionSetList)
    }

    fun getQuestionSetDetail(questionSetId: Long): GetQuestionSetDetailResponse {

        val user = SecurityUtil.getCurrentUser()

        val questionSetDetail = questionSetId.run {
            questionSetsRepository.queryQuestionSetDetailDtoById(user, questionSetId)
                ?: throw QuestionSetNotFoundException
        }

        val setQuestionList = setQuestionRepository.findAllBySetId(questionSetDetail.questionSetId)
        val questionList = questionsRepository.findByIdIn(setQuestionList.map { it.question.id })

        return GetQuestionSetDetailResponse.of(
            questionSetDetail = questionSetDetail,
            questionList = questionList,
            user = user
        )
    }

    fun answerQuestionSet(questionSetId: Long){
        val user = SecurityUtil.getCurrentUser()

        val questionSet = questionSetsRepository.findByIdOrNull(questionSetId)?: throw QuestionSetNotFoundException

        questionSolvingHistoryRepository.save(
            QuestionSolvingHistory(
                user = user,
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

        difficultyRepository.save(
            difficulty.also { difficulty.addCount(difficultyLevel)}
        )

        return DifficultyResponse(
            veryEasy = difficulty.getPercentage(DifficultyLevel.VERY_EASY),
            easy = difficulty.getPercentage(DifficultyLevel.EASY),
            normal = difficulty.getPercentage(DifficultyLevel.NORMAL),
            hard = difficulty.getPercentage(DifficultyLevel.HARD),
            veryHard = difficulty.getPercentage(DifficultyLevel.VERY_HARD),
        )
    }

    fun getQuestionSetRank(request: GetQuestionSetRankRequest): QuestionSetListResponse {
        val user = SecurityUtil.getCurrentUser()

        val questionSetList = questionSetsRepository.queryQuestionSetDtoOrderByLike(
            user = user,
            page = request.page
        )

        return QuestionSetRankResponse.of(
            page = request.page,
            pageResponse = questionSetList
        )
    }

    fun questionFavorite(questionId: Long): FavoriteResponse {
        val user = SecurityUtil.getCurrentUser()

        val problem = questionsRepository.findByIdOrNull(questionId)?.problem
            ?: throw QuestionNotFoundException

        return favorite(user, problem)
    }

    fun questionSetFavorite(questionSetId: Long): FavoriteResponse {
        val user = SecurityUtil.getCurrentUser()

        val problem = questionSetsRepository.findByIdOrNull(questionSetId)?.problem
            ?: throw QuestionSetNotFoundException

        return favorite(user, problem)
    }

    fun favorite(user: User, problem: Problem): FavoriteResponse{
        val id = FavoriteId(
            problemId = problem.id,
            userId = user.id,
        )

        return favoriteRepository.findById(id)?.let {
            favoriteRepository.deleteById(id)
            FavoriteResponse(false)
        } ?: run {
            favoriteRepository.save(
                Favorite(
                    id = id,
                    problem = problem,
                    user = user
                )
            )
            FavoriteResponse(true)
        }
    }

    fun othersAnswer(questionId: Long, request: GetOthersAnswerRequest): OthersAnswerResponse {
        val user = SecurityUtil.getCurrentUser()
        val answerList = answersRepository
            .queryAnswerByQuestionIdOrderByLikeCount(request.page, questionId)
        return OthersAnswerResponse.of(
            pageResponse = answerList,
            user = user
        )
    }
}

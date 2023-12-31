package kr.hs.dsm.inq.domain.question.presentation

import javax.validation.Valid
import kr.hs.dsm.inq.domain.question.persistence.Category
import kr.hs.dsm.inq.domain.question.persistence.DifficultyLevel
import kr.hs.dsm.inq.domain.question.presentation.dto.*
import kr.hs.dsm.inq.domain.question.service.QuestionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RequestMapping("/question")
@RestController
class QuestionController(
    private val questionService: QuestionService
) {

    @PostMapping
    fun createQuestion(@Valid @RequestBody request: CreateQuestionRequest): CreateQuestionResponses {
        return questionService.createQuestion(request)
    }

    @GetMapping
    fun getQuestionList(@Valid @ModelAttribute request: GetQuestionListRequest): QuestionListResponse {
        return questionService.getQuestionList(request)
    }

    @GetMapping("/rank")
    fun getQuestionRank(@Valid @ModelAttribute request: GetQuestionRankRequest): QuestionListResponse {
        return questionService.getQuestionRank(request)
    }

    @GetMapping("/today")
    fun getTodayQuestion(): QuestionResponse {
        return questionService.getTodayQuestion()
    }

    @GetMapping("/random")
    fun getRandomQuestion(@Valid @ModelAttribute request: GetRandomQuestionRequest): QuestionResponse {
        return questionService.getRandomQuestion(
            category = request.category
        )
    }

    @GetMapping("/popular")
    fun getPopularQuestion(): QuestionListResponse {
        return questionService.getPopularQuestion()
    }

    @GetMapping("/set/popular")
    fun getPopularQuestionSet(): QuestionSetListResponse {
        return questionService.getPopularQuestionSet()
    }

    @GetMapping("/favorite")
    fun getFavoriteQuestion(): QuestionListResponse {
        return questionService.getFavoriteQuestion()
    }

    @GetMapping("/favorite/set")
    fun getFavoriteQuestionSet(): QuestionSetListResponse {
        return questionService.getFavoriteQuestionSet()
    }

    @GetMapping("/{question-id}")
    fun getQuestionDetail(@PathVariable("question-id") questionId: Long): QuestionDetailResponse {
        return questionService.getQuestionDetail(questionId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{question-id}/answer")
    fun answerQuestion(
        @PathVariable("question-id") questionId: Long,
        @RequestBody request: AnswerRequest
    ) {
        return questionService.answerQuestion(questionId, request)
    }

    @GetMapping("/tag")
    fun getTags(@RequestParam category: Category?): TagListResponse {
        return questionService.getTagList(category)
    }

    @PostMapping("/answer/{answer-id}/like")
    fun likeAnswer(@PathVariable("answer-id") answerId: Long): LikeResponse {
        return questionService.likeAnswer(answerId)
    }

    @PostMapping("/answer/{answer-id}/dislike")
    fun dislikeAnswer(@PathVariable("answer-id") answerId: Long): DislikeResponse {
        return questionService.dislikeAnswer(answerId)
    }

    @PostMapping("/set/{question-set-id}/like")
    fun likeQuestionSet(@PathVariable("question-set-id") questionSetId: Long): LikeResponse {
        return questionService.likeQuestionSet(questionSetId)
    }

    @PostMapping("/set/{question-set-id}/dislike")
    fun dislikeQuestionSet(@PathVariable("question-set-id") questionSetId: Long): DislikeResponse {
        return questionService.dislikeQuestionSet(questionSetId)
    }

    @PostMapping("/set")
    fun registerQuestionSets(@RequestBody request: QuestionSetsRequest): RegisterQuestionSetsResponse{
        return questionService.registerQuestionSet(request)
    }

    @GetMapping("/set")
    fun getQuestionSets(@Valid @ModelAttribute request: GetQuestionSetsRequest): QuestionSetListResponse{
        return questionService.getQuestionSet(request)
    }

    @GetMapping("/set/{question-set-id}")
    fun getQuestionSetDetail(@PathVariable("question-set-id") questionSetId: Long): GetQuestionSetDetailResponse {
        return questionService.getQuestionSetDetail(questionSetId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/set/{question-set-id}")
    fun answerQuestionSet(@PathVariable("question-set-id") questionSetId: Long) {
        return questionService.answerQuestionSet(questionSetId)
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{question-id}/set")
    fun answerQuestionInQuestionSet(
        @PathVariable("question-id") questionId: Long,
        @RequestBody answerRequest: AnswerRequest
    ) {
        return questionService.answerQuestionInQuestionSet(questionId, answerRequest)
    }

    @PostMapping("/{question-id}/difficulty")
    fun assessDifficulty(
        @PathVariable("question-id") questionId: Long,
        @RequestParam level: DifficultyLevel
    ): DifficultyResponse {
        return questionService.assessDifficulty(
            questionId = questionId,
            difficultyLevel = level
        )
    }

    @GetMapping("/set/rank")
    fun getQuestionSetRank(@Valid @ModelAttribute request: GetQuestionSetRankRequest): QuestionSetListResponse {
        return questionService.getQuestionSetRank(request)
    }

    @PostMapping("/{question-id}/favorite")
    fun questionFavorite(@PathVariable("question-id") questionId: Long): FavoriteResponse{
        return questionService.questionFavorite(questionId)
    }

    @PostMapping("/set/{question-set-id}/favorite")
    fun questionSetFavorite(@PathVariable("question-set-id") questionSetId: Long): FavoriteResponse{
        return questionService.questionSetFavorite(questionSetId)
    }

    @GetMapping("/{question-id}/answer")
    fun othersAnswer(@PathVariable("question-id") questionId: Long,
                     @Valid @ModelAttribute request: GetOthersAnswerRequest): OthersAnswerResponse {
        return questionService.othersAnswer(questionId, request)
    }
}
package kr.hs.dsm.inq.domain.user.service

import kr.hs.dsm.inq.common.dto.TokenResponse
import kr.hs.dsm.inq.common.util.PageResponse
import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionDetailDto
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionSetDto
import kr.hs.dsm.inq.domain.question.persistence.repository.QuestionSetsRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.QuestionsRepository
import kr.hs.dsm.inq.domain.question.presentation.dto.QuestionSetListResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.UserQuestionResponse
import kr.hs.dsm.inq.domain.user.exception.AttendanceNotFound
import kr.hs.dsm.inq.domain.user.exception.PasswordMismatchException
import kr.hs.dsm.inq.domain.user.exception.UserAlreadyExist
import kr.hs.dsm.inq.domain.user.exception.UserNotFound
import kr.hs.dsm.inq.domain.user.persistence.User
import kr.hs.dsm.inq.domain.user.persistence.repository.AttendanceRepository
import kr.hs.dsm.inq.domain.user.presentation.dto.*
import kr.hs.dsm.inq.global.security.token.JwtGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional
import kr.hs.dsm.inq.domain.user.persistence.repository.UserRepository

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtGenerator: JwtGenerator,
    private val attendanceRepository: AttendanceRepository,
    private val questionsRepository: QuestionsRepository,
    private val questionSetsRepository: QuestionSetsRepository
) {

    fun signIn(request: UserSignInRequest): TokenResponse {
        val user = userRepository.findByAccountId(request.accountId)
            ?: throw UserNotFound

        if (!passwordEncoder.matches(request.password, user.password)) {
            throw PasswordMismatchException
        }

        return jwtGenerator.receiveToken(user.id)
    }

    fun signUp(request: UserSignUpRequest) {
        if (userRepository.existsByAccountId(request.accountId)) {
            throw UserAlreadyExist
        }

        userRepository.save(
            User(
                username = request.username,
                job = request.job,
                jobDuration = request.jobDuration,
                accountId = request.accountId,
                password = passwordEncoder.encode(request.password),
                joinDate = LocalDateTime.now()
            )
        )
    }

    fun queryUserInfo(): UserInfoResponse {
        val user = SecurityUtil.getCurrentUser()

        return UserInfoResponse(
            username = user.username,
            joinDate = user.joinDate.toLocalDate(),
            coin = user.coin,
            job = user.job,
            jobDuration = user.jobDuration
        )
    }

    @Transactional
    fun updateUserInfo(request: UpdateUserInfoRequest) {
        val user = SecurityUtil.getCurrentUser()

        user.updateInfo(request.username, request.job, request.jobDuration)

        userRepository.save(user)
    }

    fun queryUserAttendance(): UserAttendanceResponse {
        val user = SecurityUtil.getCurrentUser()

        val attendance =  attendanceRepository.findByUserId(user.id)
            ?: throw AttendanceNotFound

        return UserAttendanceResponse(
            monday = attendance.monday,
            tuesday = attendance.tuesday,
            wednesday = attendance.wednesday,
            thursday = attendance.thursday,
            friday = attendance.friday,
            saturday = attendance.saturday,
            sunday = attendance.sunday
        )
    }

    fun getMyQuestion(request: GetMyQuestionRequest): List<UserQuestionResponse> {
        val user = SecurityUtil.getCurrentUser()

        val usersQuestions = questionsRepository.queryQuestionDtoByWriterId(request.page, user)

        return usersQuestions.list.map {
            UserQuestionResponse.of(
                questionDetail = QuestionDetailDto(
                    questionId = it.questionId,
                    authorId = it.authorId,
                    username = it.username,
                    job = it.job,
                    jobDuration = it.jobDuration,
                    question = it.question,
                    category = it.category,
                    tagList = it.tagList,
                    isFavorite = it.isFavorite,
                    createdAt = it.createdAt
                ),
                exemplaryAnswer = it.exemplaryAnswer
            )
        }
    }

    fun getMyQuestionSet(request: GetMyQuestionRequest): QuestionSetListResponse {
        val user = SecurityUtil.getCurrentUser()

        val userQuestionSets = questionSetsRepository.queryQuestionSetDtoByWriterId(request.page, user)

        return QuestionSetListResponse.of(userQuestionSets)
    }
}
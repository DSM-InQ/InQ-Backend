package kr.hs.dsm.inq.domain.user.service

import kr.hs.dsm.inq.common.dto.TokenResponse
import kr.hs.dsm.inq.common.util.SecurityUtil
import kr.hs.dsm.inq.domain.question.persistence.repository.AnswersRepository
import kr.hs.dsm.inq.domain.question.persistence.dto.QuestionDetailDto
import kr.hs.dsm.inq.domain.question.persistence.repository.QuestionSetsRepository
import kr.hs.dsm.inq.domain.question.persistence.repository.QuestionsRepository
import kr.hs.dsm.inq.domain.question.presentation.dto.QuestionSetListResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.UserQuestionListResponse
import kr.hs.dsm.inq.domain.question.presentation.dto.UserQuestionResponse
import kr.hs.dsm.inq.domain.user.exception.AttendanceNotFound
import kr.hs.dsm.inq.domain.user.exception.PasswordMismatchException
import kr.hs.dsm.inq.domain.user.exception.UserAlreadyExist
import kr.hs.dsm.inq.domain.user.exception.UserNotFound
import kr.hs.dsm.inq.domain.user.persistence.Attendance
import kr.hs.dsm.inq.domain.user.persistence.User
import kr.hs.dsm.inq.domain.user.persistence.repository.AttendanceRepository
import kr.hs.dsm.inq.domain.user.presentation.dto.*
import kr.hs.dsm.inq.global.security.token.JwtGenerator
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.transaction.Transactional
import kr.hs.dsm.inq.domain.user.persistence.repository.UserRepository
import java.time.DayOfWeek
import java.time.LocalDate

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtGenerator: JwtGenerator,
    private val attendanceRepository: AttendanceRepository,
    private val answersRepository: AnswersRepository,
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

        val attendance = attendanceRepository.findByUserId(user.id)
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

    fun queryUserAnswered(request: GetUserAnsweredRequest): QuestionUserAnsweredResponse {
        val user = SecurityUtil.getCurrentUser()

        val questionList = answersRepository.querySolvedQuestionDtoByUserId(user.id, request.page)
        val questionSetList = answersRepository.querySolvedQuestionSetDtoByUserId(user.id, request.page)
        val questionSetDetailsList = questionSetList.list.flatMap {
            answersRepository.queryQuestionDtoByQuestionSetId(
                userId = user.id,
                questionSetId = it.questionSetId
            )
        }

        return QuestionUserAnsweredResponse.of(questionList, questionSetList, questionSetDetailsList)
    }

    fun getMyQuestion(request: GetMyQuestionRequest): UserQuestionListResponse {
        val user = SecurityUtil.getCurrentUser()

        val usersQuestions = questionsRepository.queryQuestionDtoByWriterId(request.page, user)

        return UserQuestionListResponse.of(usersQuestions)
    }

    fun getMyQuestionSet(request: GetMyQuestionRequest): QuestionSetListResponse {
        val user = SecurityUtil.getCurrentUser()

        val userQuestionSets = questionSetsRepository.queryQuestionSetDtoByWriter(request.page, user)

        return QuestionSetListResponse.of(userQuestionSets)
    }

    @Transactional
    fun userAttendanceCheck() {
        val user = SecurityUtil.getCurrentUser()
        val today = LocalDate.now().dayOfWeek

        val attendance = attendanceRepository.findByUserId(user.id)
            ?: attendanceRepository.save(Attendance(user = user))

        if (today == DayOfWeek.MONDAY)
            attendance.initializeAttendance()

        attendance.attendanceCheck(today)
        attendanceRepository.save(attendance)

        user.addCoin()
        userRepository.save(user)
    }
}

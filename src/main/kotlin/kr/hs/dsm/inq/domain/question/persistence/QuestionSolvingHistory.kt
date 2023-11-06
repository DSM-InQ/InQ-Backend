package kr.hs.dsm.inq.domain.question.persistence

import kr.hs.dsm.inq.domain.user.persistence.User
import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "tbl_question_solving_history")
@Entity
class QuestionSolvingHistory(
    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BIGINT", nullable = false)
    var user: User,

    @Column(columnDefinition = "DATETIME(6)", nullable = false)
    var solvedAt: LocalDateTime = LocalDateTime.now(),

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem", columnDefinition = "BIGINT", nullable = false)
    var problem: Problem
)

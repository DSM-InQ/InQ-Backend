package kr.hs.dsm.inq.domain.question.persistence

import kr.hs.dsm.inq.domain.user.persistence.User
import java.io.Serializable
import java.time.LocalDateTime
import java.util.Date
import javax.persistence.*

@Table(name = "tbl_question_solving_history")
@Entity
class QuestionSolvingHistory (
    @Id
    var id: Long,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BIGINT", nullable = false)
    var userId: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", columnDefinition = "BIGINT")
    var questionId: Questions,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_set_id", columnDefinition = "BIGINT",)
    var questionSetID: QuestionSets,

    @Column(columnDefinition = "VARCHAR(30)")
    @Enumerated(EnumType.STRING)
    var type: String,

    @Column(columnDefinition = "DATETIME(6)", nullable = false)
    var solvedAt: LocalDateTime = LocalDateTime.now(),
)

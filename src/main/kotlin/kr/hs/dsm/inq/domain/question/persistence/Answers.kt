package kr.hs.dsm.inq.domain.question.persistence

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import kr.hs.dsm.inq.domain.user.persistence.User

@Table(name = "tbl_answer")
@Entity
class Answers(
    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(columnDefinition = "BIT(1)", nullable = false)
    var isExamplary: Boolean,

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    var answer: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", columnDefinition = "BIGINT", nullable = false)
    var questions: Questions,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", columnDefinition = "BIGINT", nullable = false)
    var writer: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", columnDefinition = "BIGINT", nullable = false)
    var post: Post = Post(),

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    val createdAt: LocalDateTime = LocalDateTime.now(),
)
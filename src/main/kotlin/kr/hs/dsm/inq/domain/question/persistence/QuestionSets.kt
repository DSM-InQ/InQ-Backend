package kr.hs.dsm.inq.domain.question.persistence

import java.time.LocalDateTime
import javax.persistence.*

@Table(name = "tbl_question_set")
@Entity()
class QuestionSets (
    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    var name: String,

    @Column(columnDefinition = "DATETIME(6)", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(columnDefinition = "INT", nullable = false)
    var answerCount: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", columnDefinition = "BIGINT",nullable = false)
    var postId: Post,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", columnDefinition = "BITINT", nullable = false)
    var problemId: Problem,
)
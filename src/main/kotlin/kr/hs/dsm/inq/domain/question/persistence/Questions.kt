package kr.hs.dsm.inq.domain.question.persistence

import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import kr.hs.dsm.inq.domain.user.persistence.User

@Table(name = "tbl_question")
@Entity
data class Questions(

    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    var question: String,

    @Column(columnDefinition = "INT", nullable = false)
    var answerCount: Int = 0,

    @Column(columnDefinition = "INT", nullable = false)
    var likeCount: Int = 0,

    @Column(columnDefinition = "INT", nullable = false)
    var dislikeCount: Int = 0,

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", columnDefinition = "BIGINT", nullable = false)
    var author: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "problem_id", columnDefinition = "BIGINT", nullable = false)
    var problem: Problem,

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    val createdAt: LocalDateTime = LocalDateTime.now()

)
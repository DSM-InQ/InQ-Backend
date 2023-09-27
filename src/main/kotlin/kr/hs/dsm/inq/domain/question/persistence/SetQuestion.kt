package kr.hs.dsm.inq.domain.question.persistence

import java.io.Serializable
import javax.persistence.*

@Table(name = "tbl_set_question")
@Entity
class SetQuestion (
    @EmbeddedId
    val id: SetQuestionID,

    @MapsId("setId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", columnDefinition = "BIGINT", nullable = false)
    var setId: QuestionSets,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", columnDefinition = "BIGINT", nullable = false)
    var questionId: Questions,

    @Column(columnDefinition = "INT", nullable = false)
    var index: Int,
)

@Embeddable
data class SetQuestionID (
    @Column
    val setId: Long,

    @Column
    val questionId: Long
) : Serializable
package kr.hs.dsm.inq.domain.question.persistence

import java.io.Serializable
import javax.persistence.*

@Table(name = "tbl_set_question")
@Entity
class SetQuestion (
    @EmbeddedId
    val id: SetQuestionId,

    @MapsId("setId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "set_id", columnDefinition = "BIGINT", nullable = false)
    var setId: QuestionSets,

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", columnDefinition = "BIGINT", nullable = false)
    var questionId: Questions,

    @Column(columnDefinition = "INT", nullable = false)
    var questionIndex: Int,
)

@Embeddable
data class SetQuestionId (
    @Column
    val setId: Long,

    @Column
    val questionId: Long
) : Serializable
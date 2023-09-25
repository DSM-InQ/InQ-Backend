package kr.hs.dsm.inq.domain.question.persistence

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table


@Table(name = "tbl_qustion_tag")
@Entity
class QuestionTags(

    @EmbeddedId
    val id: QuestionTagsId,

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", columnDefinition = "BIGINT", nullable = false)
    val questions: Questions,

    @MapsId("tagId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tag_id", columnDefinition = "BIGINT", nullable = false)
    val tags: Tags

)

@Embeddable
data class QuestionTagsId(

    @Column
    val questionId: Long,

    @Column
    val tagId: Long

) : Serializable
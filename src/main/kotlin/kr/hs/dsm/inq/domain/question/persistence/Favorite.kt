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
import kr.hs.dsm.inq.domain.user.persistence.User


@Table(name = "tbl_favorite")
@Entity
class Favorite(

    @EmbeddedId
    val id: FavoriteId,

    @MapsId("questionId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", columnDefinition = "BIGINT", nullable = false)
    val questions: Questions,

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BIGINT", nullable = false)
    val user: User
)

@Embeddable
data class FavoriteId(

    @Column
    val questionId: Long,

    @Column
    val userId: Long

) : Serializable
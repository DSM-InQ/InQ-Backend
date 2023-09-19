package kr.hs.dsm.inq.domain.question.persistence

import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Embeddable
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MapsId
import javax.persistence.Table
import kr.hs.dsm.inq.domain.user.persistence.User

@Table(name = "tbl_like")
@Entity
class Like(

    @EmbeddedId
    val id: LikeId,

    @MapsId("postId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", columnDefinition = "BIGINT", nullable = false)
    val post: Post,

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", columnDefinition = "BIGINT", nullable = false)
    val user: User,

    @Column(columnDefinition = "BIT(1)", nullable = false)
    val isLiked: Boolean
)

@Embeddable
data class LikeId(

    @Column
    val postId: Long,

    @Column
    val userId: Long

) : Serializable
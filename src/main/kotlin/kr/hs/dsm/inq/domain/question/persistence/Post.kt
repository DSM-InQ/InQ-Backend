package kr.hs.dsm.inq.domain.question.persistence

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table


@Table(name = "tbl_post")
@Entity
class Post(
    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(columnDefinition = "INT", nullable = false)
    var likeCount: Int = 0,

    @Column(columnDefinition = "INT", nullable = false)
    var dislikeCount: Int = 0
) {
    fun addLikeCount() {
        likeCount += 1
    }

    fun addDislikeCount() {
        dislikeCount += 1
    }

    fun reduceLikeCount() {
        likeCount -= 1
    }

    fun reduceDislikeCount() {
        dislikeCount -= 1
    }
}
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
import javax.persistence.MapsId
import javax.persistence.Table
import kr.hs.dsm.inq.domain.user.persistence.User

@Table(name = "tbl_comment")
@Entity
class Comments(

    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(columnDefinition = "VARCHAR(1000)", nullable = false)
    val comment: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", columnDefinition = "BIGINT", nullable = false)
    var writer: User,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", columnDefinition = "BIGINT", nullable = false)
    var post: Post,

    @Column(columnDefinition = "BIT(1)", nullable = false)
    val isPrivate: Boolean,

    @Column(nullable = false, updatable = false, columnDefinition = "DATETIME(6)")
    val createdAt: LocalDateTime = LocalDateTime.now()

)
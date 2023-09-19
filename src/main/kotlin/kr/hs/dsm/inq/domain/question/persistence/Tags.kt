package kr.hs.dsm.inq.domain.question.persistence

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table


@Table(name = "tbl_tag")
@Entity
data class Tags(

    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    var tag: String,

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category
)
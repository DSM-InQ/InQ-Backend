package kr.hs.dsm.inq.domain.question.persistence

import javax.persistence.*

@Table(name = "tbl_problem")
@Entity
data class Problem (
    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    val id: Long = 0L,

    @Column(columnDefinition = "VARCHAR(30)", nullable = false)
    @Enumerated(EnumType.STRING)
    val type: ProblemType
)

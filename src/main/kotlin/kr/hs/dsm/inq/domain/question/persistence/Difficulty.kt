package kr.hs.dsm.inq.domain.question.persistence

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table


@Table(name = "tbl_difficulity")
@Entity
class Difficulty(

    @Id
    @Column(columnDefinition = "BIGINT", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0L,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questions_id", columnDefinition = "BIGINT", nullable = false)
    var questions: Questions,

    @Column(columnDefinition = "BIGINT", nullable = false)
    var veryEasyCount: Int = 0,

    @Column(columnDefinition = "BIGINT", nullable = false)
    var easyCount: Int = 0,

    @Column(columnDefinition = "BIGINT", nullable = false)
    var normalCount: Int = 0,

    @Column(columnDefinition = "BIGINT", nullable = false)
    var hardCount: Int = 0,

    @Column(columnDefinition = "BIGINT", nullable = false)
    var veryHardCount: Int = 0,
) {

    var total = veryEasyCount + easyCount + normalCount + hardCount + veryHardCount

    fun getPercentage(difficultyLevel: DifficultyLevel): Int {
        return getCount(difficultyLevel) * 100 / total
    }

    private fun getCount(difficultyLevel: DifficultyLevel): Int {
        return when (difficultyLevel) {
            DifficultyLevel.VERY_EASY -> veryEasyCount
            DifficultyLevel.EASY -> easyCount
            DifficultyLevel.NORMAL -> normalCount
            DifficultyLevel.HARD -> hardCount
            DifficultyLevel.VERY_HARD -> veryHardCount
        }
    }

    fun addCount(difficultyLevel: DifficultyLevel) {
        when (difficultyLevel) {
            DifficultyLevel.VERY_EASY -> veryEasyCount++
            DifficultyLevel.EASY -> easyCount++
            DifficultyLevel.NORMAL -> normalCount++
            DifficultyLevel.HARD -> hardCount++
            DifficultyLevel.VERY_HARD -> veryHardCount
        }
    }
}

enum class DifficultyLevel {
    VERY_EASY,
    EASY,
    NORMAL,
    HARD,
    VERY_HARD
}
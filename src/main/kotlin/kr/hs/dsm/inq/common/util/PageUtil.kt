package kr.hs.dsm.inq.common.util

import com.querydsl.jpa.impl.JPAQuery
import kr.hs.dsm.inq.common.util.PageUtil.getLimit
import kr.hs.dsm.inq.common.util.PageUtil.getOffset

object PageUtil {
    const val pageSize = 5L
    fun getOffset(page: Long) = page * pageSize
    fun getLimit() = pageSize + 1
    fun <T> hasNext(list: List<T>) = list.size > pageSize
}

/**
 * JPAQuery에 주어진 page에 대한 offset과 limit을 지정하는 extension function
 */
fun <T> JPAQuery<T>.offsetAndLimit(page: Long): JPAQuery<T> =
    offset(getOffset(page))
        .limit(getLimit())

/**
 * repository 계층에서 page 결과를 반환하기 위한 class
 */
class PageResponse<T>(
    val hasNext: Boolean,
    val list: List<T>
)
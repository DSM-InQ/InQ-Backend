package kr.hs.dsm.inq.common.util

import com.querydsl.jpa.impl.JPAQuery
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable

val defaultPage: Pageable = PageRequest.of(0, 15)

object PageUtil {
    private const val pageSize = 5L

    private fun <T> List<T>.safeSlice(startIndex: Int, endIndex: Int): List<T> {
        val start = startIndex.coerceAtLeast(0)
        val end = endIndex.coerceAtMost(size - 1)
        return slice(start..end)
    }

    fun getOffset(page: Long) = page * pageSize
    fun <T> toPageResponse(page: Long, list: List<T>): PageResponse<T> {
        val offset = getOffset(page).toInt()
        val sliceList = list.safeSlice(offset, offset + pageSize.toInt() - 1)
        return PageResponse(
            list = sliceList,
            hasNext = list.safeSlice(offset, offset + pageSize.toInt()).size > sliceList.size
        )
    }
}

/**
 * repository 계층에서 page 결과를 반환하기 위한 class
 */
class PageResponse<T>(
    val hasNext: Boolean,
    val list: List<T>
)
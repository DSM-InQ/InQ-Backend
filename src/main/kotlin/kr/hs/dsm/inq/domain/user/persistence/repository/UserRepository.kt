package kr.hs.dsm.inq.domain.user.persistence.repository

import kr.hs.dsm.inq.domain.user.persistence.User
import org.springframework.data.repository.CrudRepository

interface UserRepository : CrudRepository<User, Long> {

    fun existsByAccountId(accountId: String): Boolean

    fun findByAccountId(accountId: String): User?
}
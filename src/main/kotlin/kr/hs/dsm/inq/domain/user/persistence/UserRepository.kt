package kr.hs.dsm.inq.domain.user.persistence

import org.springframework.data.repository.CrudRepository

interface UserRepository: CrudRepository<User, Long> {

    fun existsByAccountId(accountId: String): Boolean

    fun findByAccountId(accountId: String): User?
}
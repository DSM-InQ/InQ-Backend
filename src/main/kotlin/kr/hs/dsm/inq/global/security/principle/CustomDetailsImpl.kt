package kr.hs.dsm.inq.global.security.principle

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import kr.hs.dsm.inq.domain.user.persistence.User

class CustomDetailsImpl(
    override val user: User
) : UserDetails, CustomDetails {

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> = mutableListOf()

    override fun getPassword(): String? = null

    override fun getUsername(): String = user.id.toString()

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}

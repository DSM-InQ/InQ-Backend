package kr.hs.dsm.inq.global.security.token

import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Header
import io.jsonwebtoken.InvalidClaimException
import io.jsonwebtoken.Jws
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import kr.hs.dsm.inq.global.error.InternalServerError
import kr.hs.dsm.inq.global.security.SecurityProperties
import kr.hs.dsm.inq.global.security.exception.ExpiredTokenException
import kr.hs.dsm.inq.global.security.exception.InvalidTokenException
import kr.hs.dsm.inq.global.security.exception.UnexpectedTokenException
import kr.hs.dsm.inq.global.security.principle.CustomDetailsService

@Component
class JwtParser(
    private val securityProperties: SecurityProperties,
    private val customDetailsService: CustomDetailsService
) {

    fun getAuthentication(token: String): Authentication {
        val claims = getClaims(token)

        if (claims.header[Header.JWT_TYPE] != JwtProperties.ACCESS) {
            throw InvalidTokenException
        }

        val userDetails = getDetails(claims.body)

        return UsernamePasswordAuthenticationToken(userDetails, "", userDetails.authorities)
    }

    private fun getClaims(token: String): Jws<Claims> {
        return try {
            Jwts.parser()
                .setSigningKey(securityProperties.secretKey)
                .parseClaimsJws(token)
        } catch (e: Exception) {
            when (e) {
                is InvalidClaimException -> throw InvalidTokenException
                is ExpiredJwtException -> throw ExpiredTokenException
                is JwtException -> throw UnexpectedTokenException
                else -> throw InternalServerError
            }
        }
    }

    private fun getDetails(body: Claims): UserDetails {
        return customDetailsService.loadUserByUsername(body.id)
    }
}

package kr.hs.dsm.inq.global.security

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import kr.hs.dsm.inq.global.filter.FilterConfig
import kr.hs.dsm.inq.global.security.token.JwtParser

@Configuration
class SecurityConfig(
    private val jwtParser: JwtParser,
    private val objectMapper: ObjectMapper
) {

    @Bean
    protected fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf().disable()
            .cors().and()

        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        http
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/auth/tokens").permitAll()
            .antMatchers(HttpMethod.GET, "/suggestion").authenticated()
            .antMatchers(HttpMethod.GET, "/suggestion/{suggestion-id}").authenticated()
            .antMatchers(HttpMethod.POST, "/suggestion/register/{suggestion-id}").authenticated()
            .antMatchers(HttpMethod.POST, "/suggestion/solve/{suggestion-id}").authenticated()
            .antMatchers(HttpMethod.DELETE, "/suggestion/{suggestion-id}").authenticated()
            .antMatchers(HttpMethod.POST, "/suggestion").permitAll()
            .antMatchers(HttpMethod.POST, "/image").permitAll()
            .antMatchers(HttpMethod.PATCH, "/institution").authenticated()
            .antMatchers(HttpMethod.GET, "/institution").authenticated()
            .antMatchers(HttpMethod.GET, "/statistic").authenticated()
            .anyRequest().denyAll()

        http
            .apply(FilterConfig(jwtParser, objectMapper))

        return http.build()
    }

    @Bean
    protected fun passwordEncoder() = BCryptPasswordEncoder()
}

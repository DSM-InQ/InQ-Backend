package kr.hs.dsm.inq.global.config

import com.querydsl.jpa.impl.JPAQueryFactory
import javax.persistence.EntityManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class QuerydslConfig(
    private val entityManager: EntityManager
) {
    @Bean
    protected fun queryFactory() = JPAQueryFactory(entityManager)
}

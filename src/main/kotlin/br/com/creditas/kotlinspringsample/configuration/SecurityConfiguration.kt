package br.com.creditas.kotlinspringsample.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.http.HttpStatus
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

@Configuration
@EnableMethodSecurity
@Profile("worker")
class WorkerSecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            cors { disable() }
            exceptionHandling { authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED) }
            authorizeRequests {
                authorize(AntPathRequestMatcher("/health"), permitAll)
                authorize(AntPathRequestMatcher("/metrics"), permitAll)
                authorize(anyRequest, denyAll)
            }
        }
        return http.build()
    }
}

@Configuration
@EnableMethodSecurity
@Profile("!worker && !dbmigration")
class WebSecurityConfiguration {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            sessionManagement { sessionCreationPolicy = SessionCreationPolicy.STATELESS }
            csrf { disable() }
            cors { disable() }
            exceptionHandling { authenticationEntryPoint = HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED) }
            authorizeRequests {
                authorize(AntPathRequestMatcher("/health"), permitAll)
                authorize(AntPathRequestMatcher("/metrics"), permitAll)
                authorize(AntPathRequestMatcher("/api-docs.**"), permitAll)
                authorize(AntPathRequestMatcher("/configuration/ui"), permitAll)
                authorize(AntPathRequestMatcher("/swagger-resources/**"), permitAll)
                authorize(AntPathRequestMatcher("/swagger-ui/**"), permitAll)
                authorize(AntPathRequestMatcher("/configuration/security"), permitAll)
                authorize(AntPathRequestMatcher("/swagger-ui.html"), permitAll)
                authorize(AntPathRequestMatcher("/webjars/**"), permitAll)
                authorize(anyRequest, authenticated)
            }
        }

        return http.build()
    }
}

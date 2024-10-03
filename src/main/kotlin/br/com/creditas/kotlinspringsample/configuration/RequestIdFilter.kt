package br.com.creditas.kotlinspringsample.configuration

import java.util.UUID
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.GenericFilterBean

@Component
class RequestIdFilter : GenericFilterBean() {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        if (request is HttpServletRequest) {
            MDC.put("request-id", getRequestId(request))
        }

        chain.doFilter(request, response)
    }

    private fun getRequestId(request: HttpServletRequest): String {
        return request.getHeader("X-Request-ID") ?: UUID.randomUUID().toString()
    }
}

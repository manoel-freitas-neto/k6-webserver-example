package br.com.creditas.kotlinspringsample.configuration

import br.com.creditas.auth.configuration.CreditasAuthProperties
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.models.Operation
import io.swagger.v3.oas.models.media.StringSchema
import io.swagger.v3.oas.models.parameters.Parameter
import org.springdoc.core.GroupedOpenApi
import org.springdoc.core.customizers.OperationCustomizer
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.support.beans
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.core.env.Environment
import org.springframework.web.method.HandlerMethod
import jakarta.servlet.Filter

internal val beans = beans {
    bean { registrationRequestIdFilter() }
}

private fun registrationRequestIdFilter(): FilterRegistrationBean<Filter> =
    FilterRegistrationBean<Filter>().also {
        it.filter = RequestIdFilter()
        it.order = Ordered.HIGHEST_PRECEDENCE
    }

@Suppress("UnusedPrivateClass")
@Configuration
@Profile("local,staging,prod")
@Order(Ordered.LOWEST_PRECEDENCE)
private class SpringDocConfiguration(
    @Value("\${creditas-auth.authentication-mode}") private val authenticationMode: String,
    private val environment: Environment
) {
    private val devModeEnabled =
        authenticationMode.equals(CreditasAuthProperties.AuthenticationMode.DEVELOPER.toString(), ignoreCase = true)

    @Bean
    fun openApiConfiguration(): GroupedOpenApi =
        GroupedOpenApi.builder()
            .pathsToExclude("/error|/info|/health.*")
            .build()

    @Bean
    fun customize(): OperationCustomizer =
        OperationCustomizer { operation: Operation, _: HandlerMethod ->
            operationCustomize(operation)
        }

    private fun operationCustomize(operation: Operation) =
        if (devModeEnabled && isLocalhostEnvironment()) {
            operation.parameters(
                listOf(
                    Parameter()
                        .name("X-Current-Principal-Type")
                        .description("[devModeEnabled] Id of the user/system that makes the request to this app.")
                        .`in`(ParameterIn.HEADER.toString())
                        .schema(StringSchema())
                        .required(true),

                    Parameter()
                        .name("X-Current-Principal-Id")
                        .description("[devModeEnabled] Type of the user/system that makes the request to this app.")
                        .`in`(ParameterIn.HEADER.toString())
                        .schema(StringSchema())
                        .required(true),

                    Parameter()
                        .name("X-Initial-Principal-Id")
                        .description("[devModeEnabled] Id of the user/system that initiates the request.")
                        .`in`(ParameterIn.HEADER.toString())
                        .schema(StringSchema())
                        .required(false),

                    Parameter()
                        .name("X-Initial-Principal-Type")
                        .description("[devModeEnabled] Type of the user/system that initiates the request to this app.")
                        .`in`(ParameterIn.HEADER.toString())
                        .schema(StringSchema())
                        .required(false)
                )
            )
        } else {
            operation.parameters(
                listOf(
                    Parameter()
                        .name("Authorization")
                        .description("Authorization Token")
                        .`in`(ParameterIn.HEADER.toString())
                        .schema(StringSchema())
                        .required(true),

                    Parameter()
                        .name("X-Request-Security-Context")
                        .description("")
                        .`in`(ParameterIn.HEADER.toString())
                        .schema(StringSchema())
                        .required(false)
                )
            )
        }

    private fun isLocalhostEnvironment() =
        !environment.activeProfiles.any {
            it == "prod" || it == "staging"
        }
}

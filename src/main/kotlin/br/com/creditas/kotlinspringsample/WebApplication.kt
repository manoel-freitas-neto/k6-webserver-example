package br.com.creditas.kotlinspringsample

import br.com.creditas.kotlinspringsample.configuration.beans
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class WebApplication

fun main() {
    runApplication<WebApplication>() {
        this.addInitializers(beans)
    }
}

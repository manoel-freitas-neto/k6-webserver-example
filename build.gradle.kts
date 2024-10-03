import java.net.URI
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("plugin.spring") version "1.9.23"
    kotlin("jvm") version "1.9.23"

    id("org.springframework.boot") version "3.3.2"
    id("io.spring.dependency-management") version "1.1.6"

    id("io.gitlab.arturbosch.detekt") version "1.23.6"
    id("com.github.davidmc24.gradle.plugin.avro") version "1.9.1"
}

group = "br.com.creditas"
version = "0.0.1"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()

    maven {
        url = URI(project.ext.get("artifactory_contextUrl") as String + "/libs-snapshot")
        credentials {
            username = project.ext.get("artifactory_user") as String
            password = project.ext.get("artifactory_password") as String
        }
    }

    maven {
        url = URI(project.ext.get("artifactory_contextUrl") as String + "/libs-release")

        credentials {
            username = project.ext.get("artifactory_user") as String
            password = project.ext.get("artifactory_password") as String
        }
    }
}

avro {
    fieldVisibility.set("PRIVATE")
    stringType.set("CharSequence")
}

// Dependencies
val activationVersion = "1.1.1"
val authLibVersion = "3.0.0-alpha.3"
val detektVersion = "1.23.6"
val kluentVersion = "1.73"
val logbookVersion = "3.9.0"
val logstashVersion = "7.4"
val mockkVersion = "1.13.10"
val openApiVersion = "1.8.0"
val wiremockVersion = "3.5.3"
val micrometerVersion = "1.12.5"
val datadogVersion = "1.38.0"

dependencies {
//  Basic dependencies:
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
//  Web dependencies:
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.springdoc:springdoc-openapi-ui:$openApiVersion")
//  Security Dependencies
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("br.com.creditas:authentication-spring-boot-starter:$authLibVersion")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("br.com.creditas:authentication-spring-test-support:$authLibVersion")
//  Log dependencies
    implementation("org.zalando:logbook-spring-boot-starter:$logbookVersion")
    implementation("net.logstash.logback:logstash-logback-encoder:$logstashVersion")
//  Test and lint dependencies
    testImplementation("org.amshove.kluent:kluent:$kluentVersion")
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
      exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
        exclude(module = "mockito-core")
    }
    testImplementation("io.mockk:mockk:$mockkVersion")
    detekt("io.gitlab.arturbosch.detekt:detekt-formatting:$detektVersion")
    detekt("io.gitlab.arturbosch.detekt:detekt-cli:$detektVersion")
    detekt("io.gitlab.arturbosch.detekt:detekt-rules-libraries:$detektVersion")
    testImplementation("org.wiremock:wiremock-standalone:$wiremockVersion")
// Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus:$micrometerVersion")
    implementation("com.datadoghq:dd-trace-api:$datadogVersion")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

detekt {
    toolVersion = detektVersion
    config.setFrom("./")
    config.setFrom("./detekt-config.yml")
    autoCorrect = true
}

tasks.getByName<Jar>("jar") {
    enabled = false
}
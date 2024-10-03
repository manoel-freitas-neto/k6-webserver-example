pluginManagement {
    repositories {
        maven {
            url = uri("https://packages.confluent.io/maven/")
        }
        gradlePluginPortal()
        mavenCentral()
    }
}
rootProject.name = "kotlin-spring-sample"

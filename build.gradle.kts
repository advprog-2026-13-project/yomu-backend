val jjwtVersion = "0.12.5"
val googleApiClientVersion = "2.2.0"
val googleHttpClientGsonVersion = "1.43.3"
val googleOauthClientVersion = "1.34.1"
val jacocoToolVersion = "0.8.11"

plugins {
    java
    id("org.springframework.boot") version "3.5.11"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.sonarqube") version "6.0.1.5171"
    id("jacoco")
    id("com.diffplug.spotless") version "6.25.0"
}

group = "id.ac.ui.cs.advprog"
version = "0.0.1-SNAPSHOT"
description = "yomu-backend"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

dependencyLocking {
    lockAllConfigurations()
}

repositories {
    mavenCentral()
}

val coverageExclusions = listOf(
    "**/*Application*",
    "**/*Config*",
    "**/*DTO*",
    "**/entity/**",
    "**/repository/**",
    "**/exception/**",
    "**/model/**",
    "**/forum/**",
    "**/social/**",
    "**/reading/api/**"
)

dependencyManagement {
    resolutionStrategy {
        eachDependency {
            if (requested.group == "org.junit") {
                useVersion("5.12.2")
            }
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    implementation("io.jsonwebtoken:jjwt-api:${jjwtVersion}")
    implementation("com.google.api-client:google-api-client:${googleApiClientVersion}")
    implementation("com.google.http-client:google-http-client-gson:${googleHttpClientGsonVersion}")
    implementation("com.google.oauth-client:google-oauth-client:${googleOauthClientVersion}")

    runtimeOnly("org.postgresql:postgresql")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:${jjwtVersion}")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:${jjwtVersion}")

    compileOnly("org.projectlombok:lombok")

    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
    annotationProcessor("org.projectlombok:lombok")

    developmentOnly("org.springframework.boot:spring-boot-devtools")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    testRuntimeOnly("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

jacoco {
    toolVersion = jacocoToolVersion
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
}

spotless {
    java {
        googleJavaFormat()
        target("src/**/*.java")
    }
}

sonar {
    properties {
        property("sonar.projectKey", System.getenv("SONAR_PROJECT_KEY") ?: "default_key")
        property("sonar.organization", System.getenv("SONAR_ORGANIZATION") ?: "default_org")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.coverage.jacoco.xmlReportPaths", "build/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", coverageExclusions.joinToString(", "))
    }
}
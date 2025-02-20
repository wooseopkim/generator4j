import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.errorprone.CheckSeverity

plugins {
    `java-library`
    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
}

repositories {
    mavenCentral()
}

dependencies {
    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
    implementation(libs.jspecify)

    testImplementation(libs.junit.jupiter)

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}

tasks.withType<JavaCompile>().configureEach {
    options.errorprone {
      allErrorsAsWarnings = true

      check("NullAway", CheckSeverity.ERROR)
      option("NullAway:OnlyNullMarked", true)
    }
}

spotless {
  java {
    importOrder()
    removeUnusedImports()
    cleanthat()
    palantirJavaFormat()
    formatAnnotations()
  }
}

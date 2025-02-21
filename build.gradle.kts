import net.ltgt.gradle.errorprone.errorprone
import net.ltgt.gradle.errorprone.CheckSeverity

plugins {
    `java-library`
    `maven-publish`
    alias(libs.plugins.spotless)
    alias(libs.plugins.errorprone)
}

repositories {
    mavenCentral()
}

dependencies {
    errorprone(libs.errorprone.core)
    errorprone(libs.nullaway)
    errorprone(libs.jspecify)

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

      disable("WaitNotInLoop")
      disable("NamedLikeContextualKeyword")

      error("NullAway")
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

group = "kim.wooseop"
version = "0.0.1"

// https://docs.github.com/en/packages/working-with-a-github-packages-registry/working-with-the-gradle-registry
publishing {
    repositories {
        maven {
            val githubActor = System.getenv("GITHUB_ACTOR")
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/${githubActor}/generator4j")
            credentials {
                username = githubActor
                password = System.getenv("GITHUB_TOKEN")
            }
        }
    }
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}

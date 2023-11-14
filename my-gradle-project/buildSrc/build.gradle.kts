import org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
import org.gradle.api.tasks.testing.logging.TestLogEvent.*

plugins {
  `java-gradle-plugin`
  kotlin("jvm") version "1.6.21"
}

group = "com.rogervinas"
version = "1.0"

repositories {
  mavenCentral()
}

dependencies {
  implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
  testImplementation("com.willowtreeapps.assertk:assertk-jvm:0.25")
}

gradlePlugin {
  plugins {
    create("my-buildsrc-project-plugin") {
      id = "com.rogervinas.my-buildsrc-project-plugin"
      implementationClass = "com.rogervinas.MyBuildSrcProjectPlugin"
    }
  }
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events(PASSED, SKIPPED, FAILED)
    exceptionFormat = FULL
    showExceptions = true
    showCauses = true
    showStackTraces = true
  }
}

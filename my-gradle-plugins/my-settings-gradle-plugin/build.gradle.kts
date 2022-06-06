plugins {
  `java-gradle-plugin`
  id("org.jetbrains.kotlin.jvm") version "1.6.21"
  id("maven-publish")
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
    create("my-settings-plugin") {
      id = "com.rogervinas.my-settings-plugin"
      implementationClass = "com.rogervinas.MySettingsPlugin"
    }
  }
}

val functionalTestSourceSet = sourceSets.create("functionalTest") {
}

configurations["functionalTestImplementation"].extendsFrom(configurations["testImplementation"])

val functionalTest by tasks.registering(Test::class) {
  testClassesDirs = functionalTestSourceSet.output.classesDirs
  classpath = functionalTestSourceSet.runtimeClasspath
}

gradlePlugin.testSourceSets(functionalTestSourceSet)

tasks.named<Task>("check") {
  dependsOn(functionalTest)
}

tasks.withType<Test> {
  useJUnitPlatform()
}

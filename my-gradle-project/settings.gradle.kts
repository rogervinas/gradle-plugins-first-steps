rootProject.name = "my-gradle-project"

include("my-module-1")
include("my-module-2")

pluginManagement {
  repositories {
    gradlePluginPortal()
  }

  // Just to ease development we include my-gradle-plugins project as a composite build 👇
  // To load plugins from mavenLocal and test a more realistic scenario:
  // - Execute at least once task publishToMavenLocal on my-gradle-plugins project
  // - Add mavenLocal() on section pluginManagement > repositories
  // - Comment includeBuild line below
  includeBuild("../my-gradle-plugins")

  plugins {
    id("com.rogervinas.my-settings-plugin") version "1.0"
    id("com.rogervinas.my-project-plugin") version "1.0"
  }
}

plugins {
  id("com.rogervinas.my-settings-plugin")
}

// How to create a settings plugin directly on build script 👇

class MyBuildSettingsPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {
    println("Plugin ${this.javaClass.simpleName} applied on ${settings.rootProject.name}")
    // TODO configure `settings`
  }
}

apply<MyBuildSettingsPlugin>()

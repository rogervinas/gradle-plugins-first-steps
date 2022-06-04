rootProject.name = "my-gradle-project"

include("my-module-1")
include("my-module-2")

pluginManagement {
  repositories {
    gradlePluginPortal()
  }

  includeBuild("../my-gradle-plugins")

  plugins {
    id("com.rogervinas.my-settings-gradle-plugin") version "1.0"
    id("com.rogervinas.my-project-gradle-plugin") version "1.0"
  }
}

plugins {
  id("com.rogervinas.my-settings-gradle-plugin")
}

package com.rogervinas

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class MySettingsGradlePlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {
    println("Plugin ${this.javaClass.name} applied on ${settings.rootProject.name}")
    settings.gradle.allprojects { project ->
      project.tasks.register("my-settings-task") { task ->
        task.doLast {
          println("Task my-settings-task executed on ${project.name}")
        }
      }
    }
  }
}

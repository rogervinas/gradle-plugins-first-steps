package com.rogervinas

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

class MySettingsPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {
    println("Plugin ${this.javaClass.simpleName} applied on ${settings.rootProject.name}")
    settings.gradle.allprojects { project ->
      project.tasks.register("my-settings-task") { task ->
        task.doLast {
          println("Task ${it.name} executed on ${project.name}")
        }
      }
    }
  }
}

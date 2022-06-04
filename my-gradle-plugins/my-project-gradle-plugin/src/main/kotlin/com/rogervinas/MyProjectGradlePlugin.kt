package com.rogervinas

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyProjectGradlePlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("Plugin ${this.javaClass.name} applied on ${project.name}")
    project.tasks.register("my-project-task") { task ->
      task.doLast {
        println("Task my-project-task executed on ${project.name}")
      }
    }
  }
}

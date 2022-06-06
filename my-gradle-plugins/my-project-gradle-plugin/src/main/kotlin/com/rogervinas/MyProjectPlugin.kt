package com.rogervinas

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyProjectPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("Plugin ${this.javaClass.simpleName} applied on ${project.name}")
    project.tasks.register("my-project-task") { task ->
      task.doLast {
        println("Task ${it.name} executed on ${project.name}")
      }
    }
  }
}

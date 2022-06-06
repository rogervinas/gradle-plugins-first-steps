package com.rogervinas

import org.gradle.api.Plugin
import org.gradle.api.Project

class MyBuildSrcProjectPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("Plugin ${this.javaClass.simpleName} applied on ${project.name}")
    // TODO configure project
  }
}

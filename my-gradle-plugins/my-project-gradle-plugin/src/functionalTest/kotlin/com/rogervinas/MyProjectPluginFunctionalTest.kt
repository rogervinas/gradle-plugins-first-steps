package com.rogervinas

import assertk.all
import assertk.assertThat
import assertk.assertions.contains
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import java.io.File
import kotlin.test.Test

class MyProjectPluginFunctionalTest {

  @Test
  fun `should add new task to single-project`() {
    val result = gradleRun("my-project-task", "single-project")

    assertThat(result.output).all {
      contains("Plugin MyProjectPlugin applied on single-project")
      contains("Task my-project-task executed on single-project")
    }
  }

  @Test
  fun `should add new task to multi-project`() {
    val result = gradleRun("my-project-task", "multi-project")

    assertThat(result.output).all {
      contains("Plugin MyProjectPlugin applied on multi-project")
      contains("Task my-project-task executed on multi-project")
      contains("Task my-project-task executed on module-1")
      contains("Task my-project-task executed on module-2")
    }
  }

  private fun gradleRun(taskName: String, projectName: String): BuildResult {
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments(taskName)
    runner.withProjectDir(File("src/functionalTest/resources/$projectName"))
    return runner.build()
  }
}

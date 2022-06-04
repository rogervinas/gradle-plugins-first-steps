package com.rogervinas

import assertk.assertThat
import assertk.assertions.isNotNull
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class MyProjectGradlePluginTest {

  @Test
  fun `should add new task to project`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply("com.rogervinas.my-project-gradle-plugin")

    assertThat(project.tasks.findByName("my-project-task")).isNotNull()
  }
}

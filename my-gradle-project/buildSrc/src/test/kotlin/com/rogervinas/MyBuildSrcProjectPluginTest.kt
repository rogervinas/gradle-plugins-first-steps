package com.rogervinas

import assertk.assertThat
import assertk.assertions.isNotNull
import org.gradle.testfixtures.ProjectBuilder
import org.junit.jupiter.api.Test

class MyBuildSrcProjectPluginTest {

  @Test
  fun `should add new task to project`() {
    val project = ProjectBuilder.builder().build()
    project.plugins.apply("com.rogervinas.my-buildsrc-project-plugin")

    assertThat(project.tasks.findByName("my-buildsrc-project-task")).isNotNull()
  }
}

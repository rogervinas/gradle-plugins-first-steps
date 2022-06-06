group = "com.rogervinas"
version = "1.0"

plugins {
  id("com.rogervinas.my-project-plugin")
  id("com.rogervinas.my-buildsrc-project-plugin")
}

allprojects {
  apply(plugin="com.rogervinas.my-project-plugin")
  apply(plugin="com.rogervinas.my-buildsrc-project-plugin")

  tasks.create("hello") {
    doLast {
      println("Hello from ${project.name}!")
    }
  }
}

// How to create a project plugin directly on build script 👇

class MyBuildProjectPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("Plugin ${this.javaClass.simpleName} applied on ${project.name}")
    // TODO configure `project`
  }
}

allprojects {
  apply<MyBuildProjectPlugin>()
}

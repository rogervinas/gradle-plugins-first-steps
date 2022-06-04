group = "com.rogervinas"
version = "1.0"

plugins {
  id("com.rogervinas.my-project-gradle-plugin")
}

allprojects {
  apply(plugin="com.rogervinas.my-project-gradle-plugin")

  tasks.create("hello") {
    doLast {
      println("Hello from ${project.name}!")
    }
  }
}

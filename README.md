[![CI](https://github.com/rogervinas/gradle-plugins-first-steps/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/rogervinas/gradle-plugins-first-steps/actions/workflows/gradle.yml)

# First Steps Developing Custom Gradle Plugins

Not long ago **Gradle** scared me a lot 👻 ... maybe it was because of [Groovy](https://groovy-lang.org/)? 😱

But today I am complete in ❤️ with **Gradle**! Please don't tell [Maven](https://maven.apache.org/) 😜

**Gradle** plugins allows us to reuse **build logic** across different projects, and we can implement them in any JVM compatible language: **Java**, **Kotlin**, **Groovy**, ...

In this demo we will implement basic **Gradle** plugins following the [Developing Custom Gradle Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html) documentation.

It will be fun I promise!

## Step by step

Let's follow these steps:

1. [Create plugins in the Build Script](#create-plugins-in-the-build-script)
   * [Build Script settings plugin](#build-script-settings-plugin)
   * [Build Script project plugin](#build-script-settings-plugin)
2. [Create plugins in the **buildSrc** module](#create-plugins-in-the-buildsrc-module)
3. [Create plugins in a standalone project](#create-plugins-in-a-standalone-project)
   * [Standalone settings plugin](#standalone-settings-plugin)
   * [Standalone project plugin](#standalone-project-plugin)
   * [Using the Standalone plugins](#using-the-standalone-plugins)
4. [Run this demo](#run-this-demo)
   * [Run using includeBuild](#run-using-includebuild)
   * [Run using mavenLocal](#run-using-mavenlocal)

![gradle-plugins-first-steps](doc/gradle-plugins-first-steps.png)

In this demo we will use a sample multi-module **Gradle** project named [my-gradle-project](my-gradle-project) with two modules and a custom [hello](my-gradle-project/build.gradle.kts#L13) task defined as:
```kotlin
tasks.create("hello") {
  doLast {
    println("Hello from ${project.name}!")
  }
}
```

So we can simply execute `./gradlew hello` and check all the plugins that are applied. 

### Create plugins in the Build Script

As a first step, we can define plugins directly on our build script. This is enough if we do not have to reuse them outside the build script they are defined in.

#### Build Script settings plugin

To create a settings plugin and apply it in our [settings.gradle.kts](my-gradle-project/settings.gradle.kts#L30):
```kotlin
class MyBuildSettingsPlugin : Plugin<Settings> {
  override fun apply(settings: Settings) {
    println("Plugin ${this.javaClass.simpleName} applied on ${settings.rootProject.name}")
    // TODO configure `settings`
  }
}

apply<MyBuildSettingsPlugin>()
```

For simplicity this example plugin only prints a line whenever is applied. A real plugin should do something with the `Settings` object that is passed as parameter.

Try it running `./gradlew hello` and it should print this line:
```
Plugin MyBuildSettingsPlugin applied on my-gradle-project
```

#### Build Script project plugin

To create a project plugin in our root [build.gradle.kts](my-gradle-project/build.gradle.kts#L22):
```kotlin
class MyBuildProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("Plugin ${this.javaClass.simpleName} applied on ${project.name}")
        // TODO configure `project`
    }
}
```

Then for example we can apply the plugin on all projects:
```kotlin
allprojects {
    apply<MyBuildProjectPlugin>()
}
```

Again this example plugin only prints a line whenever is applied. A real plugin should do something with the `Project` object that is passed as parameter.

Try it running `./gradlew hello` and it should print these lines:
```
> Configure project :
Plugin MyBuildProjectPlugin applied on my-gradle-project
Plugin MyBuildProjectPlugin applied on my-module-1
Plugin MyBuildProjectPlugin applied on my-module-2
```

### Create plugins in the **buildSrc** module

As a second step, we can define project plugins in a special module named [**buildSrc**](my-gradle-project/buildSrc). All plugins defined there will be **only** visible to every build script within the project.

But most important, we can add tests! 🤩

First we create [**buildSrc**](my-gradle-project/buildSrc) module under [my-gradle-project](my-gradle-project) using [**Gradle init** and the **kotlin-gradle-plugin** template](https://docs.gradle.org/current/userguide/build_init_plugin.html#sec:kotlin_gradle_plugin)

We implement the plugin in [MyBuildSrcProjectPlugin.kt](my-gradle-project/buildSrc/src/main/kotlin/com/rogervinas/MyBuildSrcProjectPlugin.kt):
```kotlin
class MyBuildSrcProjectPlugin : Plugin<Project> {
  override fun apply(project: Project) {
    println("Plugin ${this.javaClass.simpleName} applied on ${project.name}")
    project.tasks.register("my-buildsrc-project-task") { task ->
      task.doLast {
        println("Task ${task.name} executed on ${project.name}")
      }
    }
  }
}
```

We register it in [**buildSrc** > build.gradle.kts](my-gradle-project/buildSrc/build.gradle.kts), giving it an `id`:
```kotlin
gradlePlugin {
    plugins {
        create("my-buildsrc-project-plugin") {
            id = "com.rogervinas.my-buildsrc-project-plugin"
            implementationClass = "com.rogervinas.MyBuildSrcProjectPlugin"
        }
    }
}
```

And we unit test it in [MyBuildSrcProjectPluginTest.kt](my-gradle-project/buildSrc/src/test/kotlin/com/rogervinas/MyBuildSrcProjectPluginTest.kt):
```kotlin
@Test
fun `should add new task to project`() {
  val project = ProjectBuilder.builder().build()
  project.plugins.apply("com.rogervinas.my-buildsrc-project-plugin")

  assertThat(project.tasks.findByName("my-buildsrc-project-task")).isNotNull()
}
```

As you can see in this example the plugin registers a new task named `my-buildsrc-project-task`.

So now we can use it in any build script for example in root [my-gradle-project > build.gradle.kts](my-gradle-project/build.gradle.kts) applied to `allprojects`:
```kotlin
plugins {
  id("com.rogervinas.my-buildsrc-project-plugin")
}

allprojects { 
  apply(plugin = "com.rogervinas.my-buildsrc-project-plugin")
}
```

And then we can try it executing `./gradlew my-buildsrc-project-task` and it should print these lines:
```
> Configure project :
Plugin MyBuildSrcProjectPlugin applied on my-gradle-project
Plugin MyBuildSrcProjectPlugin applied on my-module-1
Plugin MyBuildSrcProjectPlugin applied on my-module-2

> Task :my-buildsrc-project-task
Task my-buildsrc-project-task executed on my-gradle-project

> Task :my-module-1:my-buildsrc-project-task
Task my-buildsrc-project-task executed on my-module-1

> Task :my-module-2:my-buildsrc-project-task
Task my-buildsrc-project-task executed on my-module-2
```

Notes:
* Apart from *unit tests* we can also add *functional tests* to the **buildSrc** module. I omitted them here for simplicity (you can see an example in the [Standalone Project](#standalone-project) section)
* We cannot define settings plugins on **buildSrc** since **Gradle** 5.x because [classes from buildSrc are no longer visible to settings scripts](https://docs.gradle.org/current/userguide/upgrading_version_5.html#classes_from_buildsrc_are_no_longer_visible_to_settings_scripts)

### Create plugins in a standalone project

As a final step, if we want to reuse plugins among all our projects and even share them with the rest of the world, we can create them in a separate project.

For this sample I've created a project [my-gradle-plugins](my-gradle-plugins) with two independent modules each using [**Gradle init** and the **kotlin-gradle-plugin** template](https://docs.gradle.org/current/userguide/build_init_plugin.html#sec:kotlin_gradle_plugin). Other templates can be used: [java-gradle-plugin](https://docs.gradle.org/current/userguide/build_init_plugin.html#sec:java_gradle_plugin) or [groovy-gradle-plugin](https://docs.gradle.org/current/userguide/build_init_plugin.html#sec:groovy_gradle_plugin) 

I've decided to create one plugin per module, but you could define many plugins in the same module.

#### Standalone settings plugin

We implement the plugin in [MySettingsPlugin.kt](my-gradle-plugins/my-settings-gradle-plugin/src/main/kotlin/com/rogervinas/MySettingsPlugin.kt):
```kotlin
class MySettingsPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        println("Plugin ${this.javaClass.simpleName} applied on ${settings.rootProject.name}")
        settings.gradle.allprojects { project ->
            project.tasks.register("my-settings-task") { task ->
                task.doLast {
                    println("Task ${task.name} executed on ${project.name}")
                }
            }
        }
    }
}
```

We register it in [build.gradle.kts](my-gradle-plugins/my-settings-gradle-plugin/build.gradle.kts), giving it an `id`:
```kotlin
gradlePlugin {
    plugins {
        create("my-settings-plugin") {
            id = "com.rogervinas.my-settings-plugin"
            implementationClass = "com.rogervinas.MySettingsPlugin"
        }
    }
}
```

And we test it in a *functional test* in [MySettingsPluginFunctionalTest.kt](my-gradle-plugins/my-settings-gradle-plugin/src/functionalTest/kotlin/com/rogervinas/MySettingsPluginFunctionalTest.kt), with real gradle projects saved under [src/functionalTest/resources](my-gradle-plugins/my-settings-gradle-plugin/src/test/resources):
```kotlin
@Test
fun `should add new task to single-project`() {
    val runner = GradleRunner.create()
    runner.forwardOutput()
    runner.withPluginClasspath()
    runner.withArguments("my-settings-task")
    runner.withProjectDir(File("src/functionalTest/resources/single-project"))
    val result = runner.build()

    assertThat(result.output).all {
        contains("Plugin MySettingsPlugin applied on single-project")
        contains("Task my-settings-task executed on single-project")
    }
}
```

Notes:
* If you check [MySettingsPluginFunctionalTest.kt](my-gradle-plugins/my-settings-gradle-plugin/src/functionalTest/kotlin/com/rogervinas/MySettingsPluginFunctionalTest.kt) you will see two tests: one for one single-project and one for one multi-module project.
* I have not found any way to *unit test* a settings plugin. For settings plugins there is no helper class like there is `org.gradle.testfixtures.ProjectBuilder` for project plugins. If you know a way please let me know! 🙏
* We use static gradle projects saved under `src/functionalTest/resources` but we can also generate gradle projects programmatically, saving them on temporary folders (check [this sample](https://docs.gradle.org/current/userguide/test_kit.html#example_using_gradlerunner_with_java_and_junit)).

#### Standalone project plugin

We implement the plugin in [MyProjectPlugin.kt](my-gradle-plugins/my-project-gradle-plugin/src/main/kotlin/com/rogervinas/MyProjectPlugin.kt):
```kotlin
class MyProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("Plugin ${this.javaClass.simpleName} applied on ${project.name}")
        project.tasks.register("my-project-task") { task ->
            task.doLast {
                println("Task ${task.name} executed on ${project.name}")
            }
        }
    }
}
```

We register it in [build.gradle.kts](my-gradle-plugins/my-project-gradle-plugin/build.gradle.kts), giving it an `id`:
```kotlin
gradlePlugin {
    plugins {
        create("my-project-plugin") {
            id = "com.rogervinas.my-project-plugin"
            implementationClass = "com.rogervinas.MyProjectPlugin"
        }
    }
}
```

We test it in a *unit test* in [MyProjectPluginTest.kt](my-gradle-plugins/my-project-gradle-plugin/src/test/kotlin/com/rogervinas/MyProjectPluginTest.kt):
```kotlin
@Test
fun `should add new task to project`() {
  val project = ProjectBuilder.builder().build()
  project.plugins.apply("com.rogervinas.my-project-plugin")

  assertThat(project.tasks.findByName("my-project-task")).isNotNull()
}
```

We test it in a *functional test* in [MyProjectPluginFunctionalTest.kt](my-gradle-plugins/my-project-gradle-plugin/src/functionalTest/kotlin/com/rogervinas/MyProjectPluginFunctionalTest.kt) with real gradle projects saved under [src/functionalTest/resources](my-gradle-plugins/my-project-gradle-plugin/src/functionalTest/resources):
```kotlin
@Test
fun `should add new task to single-project`() {
  val runner = GradleRunner.create()
  runner.forwardOutput()
  runner.withPluginClasspath()
  runner.withArguments("my-project-task")
  runner.withProjectDir(File("src/functionalTest/resources/single-project"))
  val result = runner.build()

  assertThat(result.output).all {
    contains("Plugin MyProjectPlugin applied on single-project")
    contains("Task my-project-task executed on single-project")
  }
}
```

Notes:
* If you check [MyProjectPluginFunctionalTest.kt](my-gradle-plugins/my-project-gradle-plugin/src/functionalTest/kotlin/com/rogervinas/MyProjectPluginFunctionalTest.kt) you will see two tests: one for one single-project and one for one multi-module project.
* We use static gradle projects saved under `src/functionalTest/resources` but we can also generate gradle projects programmatically, saving them on temporary folders (check [this sample](https://docs.gradle.org/current/userguide/test_kit.html#example_using_gradlerunner_with_java_and_junit)).

### Using the standalone plugins

To use the standalone plugins **locally** during development we have two alternatives:
* Using `includeBuild`: see [Run my-gradle-project using includeBuild](#run-my-gradle-project-using-includebuild)
* Publishing the plugins locally: see [Run my-gradle-project using mavenLocal](#run-my-gradle-project-using-mavenlocal)

Then we declare which version we want to use just once in [settings.gradle.kts](my-gradle-project/settings.gradle.kts):
```kotlin
pluginManagement {
  plugins {
    id("com.rogervinas.my-settings-plugin") version "1.0"
    id("com.rogervinas.my-project-plugin") version "1.0"
  }
}
```

We apply the settings plugin in [my-gradle-project > settings.gradle.kts](my-gradle-project/settings.gradle.kts):
```kotlin
plugins {
  id("com.rogervinas.my-settings-plugin")
}
```

We apply the project plugin in any build script for example in [my-gradle-project > build.gradle.kts](my-gradle-project/build.gradle.kts) applied to `allprojects`:
```kotlin
plugins {
  id("com.rogervinas.my-project-plugin")
}

allprojects {
  apply(plugin="com.rogervinas.my-project-plugin")
}
```

And finally we can publish them to any private or public repository or to [Gradle Plugin Portal](https://docs.gradle.org/current/userguide/publishing_gradle_plugins.html) 🎉

## Run this demo

### Run using includeBuild

1. Edit [my-gradle-project > settings.gradle.kts](my-gradle-project/settings.gradle.kts) and:
* Remove or comment line `mavenLocal()` in pluginManagement > repositories
* Add or uncomment line `includeBuild("../my-gradle-plugins")`

2. Execute:
```shell
cd my-gradle-project
./gradlew hello
```

If you want to know more about `includeBuild` you can read about [Composing builds](https://docs.gradle.org/current/userguide/composite_builds.html#composite_builds)

### Run using mavenLocal

1. Build and publish my-gradle-plugins locally:

```shell
cd my-gradle-plugins
./gradlew publishToMavenLocal
```

2. Edit [my-gradle-project > settings.gradle.kts](my-gradle-project/settings.gradle.kts) and:
* Add or uncomment line `mavenLocal()` in pluginManagement > repositories
* Remove or comment line `includeBuild("../my-gradle-plugins")`

3. Execute:
```shell
cd my-gradle-project
./gradlew hello
```

## Documentation

* [Developing Custom Gradle Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html)
* [Testing Gradle plugins](https://docs.gradle.org/current/userguide/testing_gradle_plugins.html)

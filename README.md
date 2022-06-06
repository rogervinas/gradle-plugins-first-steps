[![CI](https://github.com/rogervinas/gradle-plugins-first-steps/actions/workflows/gradle.yml/badge.svg?branch=main)](https://github.com/rogervinas/gradle-plugins-first-steps/actions/workflows/gradle.yml)

# First Steps Developing Custom Gradle Plugins

Not long ago **Gradle** scared me a lot 👻 ... maybe it was because of [Groovy](https://groovy-lang.org/)? who knows ...

But today I am complete in ❤️ with **Gradle**! Please don't tell [Maven](https://maven.apache.org/) 😜

**Gradle** plugins allows us to reuse build logic across different projects, and we can implement them in any JVM compatible language: **Java**, **Kotlin**, **Groovy**, ...

In this demo we will implement basic **Gradle** plugins following the [Developing Custom Gradle Plugins](https://docs.gradle.org/current/userguide/custom_plugins.html) documentation.

It will be fun I promise!

## Step by step

Let's follow these steps:

1. [Create plugins directly in the Build Script](#build-script)
   * [Build Script settings plugin](#build-script-settings-plugin)
   * [Build Script project plugin](#build-script-settings-plugin)
2. [Create plugins in the **buildSrc** module](#buildsrc-project)
3. [Create plugins in a standalone project](#standalone-project)

In this demo we will use a sample multi-module **Gradle** project named `my-gradle-project` with two modules and a custom `hello` task defined as:
```kotlin
tasks.create("hello") {
  doLast {
    println("Hello from ${project.name}!")
  }
}
```

So we can simply execute `./gradlew hello` and check all the plugins that are applied. 

![gradle-plugins-first-steps](doc/gradle-plugins-first-steps.png)

### Build Script

As a first step, we can create **Gradle** plugins directly on our build script. This is enough if we do not have to reuse them outside the build script they are defined in.

#### Build Script settings plugin

To create a settings plugin and apply it in our `settings.gradle.kts`:
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

To create a project plugin in our root `build.gradle.kts`:
```kotlin
class MyBuildProjectPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("Plugin ${this.javaClass.simpleName} applied on ${project.name}")
        // TODO configure `project`
    }
}
```

Then we can apply on all projects (root project and all modules):
```kotlin
allprojects {
    apply<MyBuildProjectPlugin>()
}
```

Again this example plugin only prints a line whenever is applied. A real plugin should do something with the `Project` object that is passed as parameter.

Try it running `./gradlew hello` and it should print these lines:
```
Plugin MyBuildProjectPlugin applied on my-gradle-project
Plugin MyBuildProjectPlugin applied on my-module-1
Plugin MyBuildProjectPlugin applied on my-module-2
```

### BuildSrc Project

### Standalone Project

## Run this demo

### Run my-gradle-project using includeBuild

1. Edit [my-gradle-project/settings.gradle.kts](my-gradle-project/settings.gradle.kts) and:
* Remove or comment line `mavenLocal()` in pluginManagement > repositories
* Add or uncomment line `includeBuild("../my-gradle-plugins")`

2. Execute:
```shell
cd my-gradle-project
./gradlew hello
```

### Run my-gradle-project using mavenLocal

1. Build and publish my-gradle-plugins locally:

```shell
cd my-gradle-plugins
./gradlew publishToMavenLocal
```

2. Edit [my-gradle-project/settings.gradle.kts](my-gradle-project/settings.gradle.kts) and:
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

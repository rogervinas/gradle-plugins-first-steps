rootProject.name = "multi-project"

include("module-1")
include("module-2")

plugins {
  id("com.rogervinas.my-settings-plugin")
}

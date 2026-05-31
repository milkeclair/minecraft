pluginManagement {
  includeBuild("../../lib/gradlePlugins")

  repositories {
    gradlePluginPortal()
    maven {
      name = "NeoForge"
      url = uri("https://maven.neoforged.net/releases")
    }
  }
}

rootProject.name = "glacage"

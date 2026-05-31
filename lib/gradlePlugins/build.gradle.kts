plugins {
  `java-gradle-plugin`
  id("com.diffplug.spotless") version "8.5.1"
}

group = "com.milkeclair"
version = "0.1.0"

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

gradlePlugin {
  plugins {
    create("windowsMinecraftClient") {
      id = "com.milkeclair.minecraft-windows-client"
      implementationClass = "com.milkeclair.minecraft.gradle.WindowsMinecraftClientPlugin"
    }
  }
}

spotless {
  java {
    googleJavaFormat()
    removeUnusedImports()
    trimTrailingWhitespace()
    endWithNewline()
  }

  kotlinGradle {
    target("*.gradle.kts", "settings.gradle.kts")
    ktlint().editorConfigOverride(
      mapOf(
        "indent_size" to "2",
        "continuation_indent_size" to "2",
        "ktlint_code_style" to "intellij_idea",
      ),
    )
    trimTrailingWhitespace()
    endWithNewline()
  }
}

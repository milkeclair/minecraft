plugins {
  `java-library`
  id("net.neoforged.moddev") version "2.0.141"
  id("com.milkeclair.minecraft-windows-client")
  id("com.diffplug.spotless") version "8.5.1"
}

val modId = providers.gradleProperty("mod_id").get()
val modGroupId = providers.gradleProperty("mod_group_id").get()
val modVersion = providers.gradleProperty("mod_version").get()
val neoForgeVersion = providers.gradleProperty("neoforge_version").get()
val clientRunDirectory = layout.projectDirectory.dir("runs/client").asFile
val serverRunDirectory = layout.projectDirectory.dir("runs/server").asFile

group = modGroupId
version = modVersion

base {
  archivesName.set(modId)
}

repositories {
  mavenCentral()
}

java {
  sourceCompatibility = JavaVersion.VERSION_21
  targetCompatibility = JavaVersion.VERSION_21
}

neoForge {
  version = neoForgeVersion
  validateAccessTransformers = true

  runs {
    create("client") {
      client()
      gameDirectory = clientRunDirectory
    }

    create("server") {
      server()
      gameDirectory = serverRunDirectory
      programArgument("--nogui")
    }
  }

  mods {
    create(modId) {
      sourceSet(sourceSets.main.get())
    }
  }

  unitTest {
    enable()
    testedMod.set(mods.named(modId))
  }
}

dependencies {
  testImplementation(platform("org.junit:junit-bom:6.0.1"))
  testImplementation("org.junit.jupiter:junit-jupiter")
  testImplementation("org.assertj:assertj-core:3.27.6")
  testImplementation("org.mockito:mockito-core:5.21.0")
  testImplementation("net.neoforged:testframework:$neoForgeVersion")
  testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

sourceSets {
  test {
    compileClasspath += sourceSets.main.get().compileClasspath
    runtimeClasspath += sourceSets.main.get().runtimeClasspath
  }
}

spotless {
  java {
    target("src/**/*.java")
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

  format("resources") {
    target("src/main/resources/**/*.json", "src/main/resources/**/*.toml", "gradle.properties")
    trimTrailingWhitespace()
    endWithNewline()
  }
}

tasks.named<Test>("test") {
  useJUnitPlatform()
}

tasks.register("verifyAll") {
  group = "verification"
  description = "Runs formatting checks and builds both Gradle build logic and this mod."

  dependsOn(
    gradle.includedBuild("gradlePlugins").task(":spotlessApply"),
    gradle.includedBuild("gradlePlugins").task(":spotlessCheck"),
    gradle.includedBuild("gradlePlugins").task(":build"),
    tasks.named("spotlessApply"),
    tasks.named("spotlessCheck"),
    tasks.named("build"),
    tasks.named("test"),
  )
}

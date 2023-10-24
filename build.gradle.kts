import java.util.*

plugins {
    `java-library`
}

repositories {
    mavenCentral()
}

dependencies {
    // Use JUnit Jupiter for testing.
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(8))
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}


// Fun with KeY
val KEY_PROJECT_FILE = file("src/main/project.key").toString()

val cfgKey = configurations.create("key")

repositories {
    maven("https://s01.oss.sonatype.org/content/repositories/iogithubwadoon-1001")
}

dependencies {
    // use local versions of the tools
    // cfgKey(
    //    "tools/key-2.10.0-exe.jar",
    //    "tools/citool-1.4.0-mini.jar")

    cfgKey("org.key-project:key.ui:2.12.1")
    cfgKey("io.github.wadoon.key:key-citool:1.6.0") {
        exclude("org.key-project:key.ui:2.12.1")
        exclude("org.key-project:key.core:2.12.1")
    }
}

tasks.create<JavaExec>("runkey") {
    classpath = cfgKey
    args = listOf(KEY_PROJECT_FILE)
    mainClass = "de.uka.ilkd.key.core.Main"
}

tasks.create<JavaExec>("keycheck") {
    classpath = cfgKey
    args = listOf("--proof-path", "src/main/proofs", "-s", "statistics.json", KEY_PROJECT_FILE)
    mainClass = "de.uka.ilkd.key.CheckerKt"
}



abstract class GenerateKeyProjectFile : DefaultTask() {
    @get:Input
    abstract val destinationFile: Property<File>

    @get:Input
    abstract val srcFolders : ListProperty<File>

    @get:Input
    abstract val classpath : ListProperty<File>

    @get:Input
    abstract val includes : ListProperty<File>


    @TaskAction
    fun generate() {
        val dest = destinationFile.get().toPath()
        dest.toFile().bufferedWriter().use { out ->
            out.write(
                """
                // This file is automatically created by `gradle generateKeyProject`
                // Created on: ${Date()}
                
                """.trimIndent()
            )

            includes.get().forEach {
                val p = dest.relativize(it.toPath())
                out.write("\\include \"$p\";\n")
            }

            srcFolders.get().forEach {
                val p = dest.relativize(it.toPath())
                out.write("\\javaSrc   \"$p\";\n")
            }

            classpath.get().forEach {
                val p = dest.relativize(it.toPath())
                out.write("\\classpath \"$p\";\n")
            }

            out.write("\n\\chooseContract\n")
        }
    }
}


tasks.create<GenerateKeyProjectFile>("generateKeyProject") {
    srcFolders = sourceSets.main.get().java.srcDirs.toList()
    classpath = sourceSets.main.get().compileClasspath.files.toList()
    destinationFile = file(KEY_PROJECT_FILE)

}

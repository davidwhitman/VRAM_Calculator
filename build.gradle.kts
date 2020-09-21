import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.10"
    application
}
group = "com.wisp"
val kotlinVersion = "1.4.10"


val vramCounterVersion = "1.3.0"
val toolname = "VRAM-Counter"
val jarFileName = "$toolname.jar"
val relativeJavaExePath = "../../jre/bin/java.exe"

repositories {
    mavenCentral()
}

tasks {
// Compile to Java 6 bytecode so that Starsector can use it
    withType<KotlinCompile>() {
        kotlinOptions.jvmTarget = "1.6"
    }

    named<Jar>("jar")
    {
        destinationDirectory.set(file("$rootDir/$toolname-$vramCounterVersion"))
        archiveFileName.set(jarFileName)

        // Set the class to run
        manifest.attributes["Main-Class"] = "MainKt"

        // Add JAR dependencies to the JAR
        configurations.runtimeClasspath.get()
            .filter { !it.isDirectory }
//            .filter { it.name in "kotlin-stdlib-$kotlinVersion.jar" }
            .map { zipTree(it) }
            .also { from(it) }

        doLast {
            File(destinationDirectory.get().asFile, "$toolname.bat")
                .writeText(
                    StringBuilder()
                        .appendln(
                            """
@echo off
IF NOT EXIST $relativeJavaExePath (
    ECHO Place $toolname folder in Starsector's mods folder.
    pause
) ELSE (
 "$relativeJavaExePath" -jar ./$jarFileName
 pause
)
"""
                        )
                        .toString()
                )

//            File(destinationDirectory.get().asFile, "$toolname.sh")
//                .writeText(
//                    StringBuilder()
//                        .appendln("#!/bin/sh")
//                        .appendln("$relativeJavaExePath -jar ./$jarFileName")
//                        .toString()
//                )

            File(destinationDirectory.get().asFile, "readme.md")
                .writeText(
                    """
# $toolname $vramCounterVersion

## Use

Place folder in Starsector's /mods folder, then launch.
Windows: Open .bat file
Not-Windows: Use .sh file

![screenshot](screenshot.png)

## Changelog

1.3.0

- Now prints out estimated usage of *enabled mods*, in addition to all found mods.
- For readability, only the currently chosen GraphicsLib settings (in 'config.properties') are shown.
- Fixed the # images count incorrectly counting all files.
- Now shows mod name, version, and id instead of mod folder name.

1.2.0

- Image channels are now accurately detected for all known cases, improving accuracy (now on par with original Python script).
- Files with '_CURRENTLY_UNUSED' in the name are ignored.
- Added configuration file for display printouts and script performance data.
- Converted to Kotlin, added .bat and .sh launchers.
- Greatly increased performance by multithreading file opening.
- Now shows with GraphicsLib (default) and with specific GraphicsLib maps disabled (configurable)

1.1.0

- Backgrounds are now only counted if larger than vanilla size and only by their increase over vanilla.

1.0.0

- Original release of Wisp's version.

Original script by Dark Revenant. Transcoded to Kotlin and edited to show more info by Wisp.
Source: [https://github.com/davidwhitman/VRAM_Calculator]
"""
                )

            File(destinationDirectory.get().asFile, "LICENSE.txt")
                .writeText(project.file("LICENSE.txt").readText())

            File(destinationDirectory.get().asFile, "config.properties")
                .writeText(
                    StringBuilder()
                        .appendln("showSkippedFiles=false")
                        .appendln("showCountedFiles=true")
                        .appendln("showPerformance=true")
                        .appendln("showGfxLibDebugOutput=false")
                        .appendln("areGfxLibNormalMapsEnabled=true")
                        .appendln("areGfxLibMaterialMapsEnabled=true")
                        .appendln("areGfxLibSurfaceMapsEnabled=true")
                        .toString()
                )

            File(destinationDirectory.get().asFile, "screenshot.png")
                .writeBytes(project.file("screenshot.png").readBytes())
        }
    }
}

application {
    mainClassName = "MainKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk7"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.3.9")
    implementation("de.siegmar:fastcsv:1.0.3")
    implementation ("com.fasterxml.jackson.core:jackson-core:2.11.2")
    implementation ("com.fasterxml.jackson.core:jackson-databind:2.11.2")
    // Comnpiled for Java 8, doesn't work
//    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:0.11.0")
}
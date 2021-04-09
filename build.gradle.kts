import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.32"
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "me.patrykanuszczyk"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    // Spigot
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    // Shadow
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    // CraftBukkit
    flatDir { dir("libs") }
}

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly("org.spigotmc:spigot-api:1.13.2-R0.1-SNAPSHOT")
    compileOnly("", "craftbukkit-1.14.4-R0.1-SNAPSHOT")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("")
    if(project.version.toString().endsWith("-SNAPSHOT")) {
        archiveVersion.set("")
    }
}
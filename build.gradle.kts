/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

import org.gradle.accessors.dm.LibrariesForLibs

plugins {
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}

allprojects {
    // Replace with your domain/project id
    group = "com.nrkei.project.dialog"
    version = "0.1.0"
}

val javaVersion = providers.gradleProperty("javaVersion").map(String::toInt).get()

subprojects {
    apply(plugin = "org.jetbrains.kotlin.jvm")

    val libs = rootProject.extensions.getByType<LibrariesForLibs>()
    val sourceSets = extensions.getByType<SourceSetContainer>()

    extensions.configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(javaVersion))
        }
    }

    dependencies {
        "testImplementation"(platform(libs.junit.bom))
        "testImplementation"(libs.junit.jupiter)
        "testRuntimeOnly"(libs.junit.platform.launcher)
        "testRuntimeOnly"(libs.junit.jupiter.engine)
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }

    tasks.named<Test>("test") {
        description = "Runs fast unit tests only."
        exclude("**/integration/**")
        include("**/unit/**")
    }

    tasks.register<Test>("integrationTest") {
        description = "Runs integration tests."
        group = LifecycleBasePlugin.VERIFICATION_GROUP

        testClassesDirs = sourceSets["test"].output.classesDirs
        classpath = sourceSets["test"].runtimeClasspath

        useJUnitPlatform()

        include("**/integration/**")

        shouldRunAfter(tasks.named("test"))
    }

    tasks.named("check") {
        dependsOn(tasks.named("integrationTest"))
    }
}

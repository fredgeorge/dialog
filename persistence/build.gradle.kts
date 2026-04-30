/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

plugins {
    alias(libs.plugins.kotlin.serialization)
    `maven-publish`
}

dependencies {
    implementation(project(":engine"))
    implementation(libs.context.engine)
    implementation(libs.context.persistence)
    implementation(libs.issue.engine)
    implementation(libs.issue.persistence)
    implementation(libs.kotlinx.serialization.json)
    testImplementation(project(":test_support"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "dialog-persistence"
        }
    }
    repositories {
        mavenLocal()
    }
}

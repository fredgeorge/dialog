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
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.context.engine)
    implementation(libs.issue.engine)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            artifactId = "dialog-engine"
        }
    }
    repositories {
        mavenLocal()
    }
}

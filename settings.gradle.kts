/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
}

// Change to your project name
rootProject.name = "dialog"
include("engine", "test_support", "tests", "persistence")

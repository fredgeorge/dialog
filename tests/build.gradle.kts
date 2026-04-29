/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

dependencies {
    implementation(project(":engine"))
    testImplementation(project(":test_support"))
    testImplementation(libs.context.engine)
    testImplementation(libs.issue.engine)
}

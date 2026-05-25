/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.api

import com.nrkei.project.dialog.api.endpoints.answerRoute
import com.nrkei.project.dialog.api.endpoints.healthRoute
import com.nrkei.project.dialog.api.endpoints.issuesRoute
import com.nrkei.project.dialog.api.endpoints.loginRoute
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRoutes() {
    routing {
        healthRoute()
        issuesRoute()
        answerRoute()
        loginRoute()
     }
 }

/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.api.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Routing.loginRoute() {
    post("/login/{conversationId?}") {
        val conversationId = call.parameters["conversationId"]?.let { UUID.fromString(it) }
            ?: UUID.randomUUID()
        val response = IssuesResponse(conversationId.toString(), issues = emptyList(), messages = emptyList())
        call.respond(HttpStatusCode.OK, response)
    }
}

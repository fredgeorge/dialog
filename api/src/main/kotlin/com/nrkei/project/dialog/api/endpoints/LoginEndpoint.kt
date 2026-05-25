/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.api.endpoints

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Routing.loginRoute() {
    post("/login/{conversationId?}") {
        val conversationId = call.parameters["conversationId"]?:"${UUID.randomUUID()}" // default ID to UUID string
        val response = IssuesResponse(conversationId, issues = emptyList(), messages = emptyList())
        call.respond(OK, response)
    }
}

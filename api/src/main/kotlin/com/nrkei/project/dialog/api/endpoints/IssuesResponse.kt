/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.api.endpoints

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class IssuesResponse(
    val conversationUUID: String,
    val issues: List<String>,
    val messages: List<String> = emptyList()
)

fun Routing.issuesRoute() {
    post("/issues/{conversationUUID}") {
        val conversationUUID = call.parameters["conversationUUID"]?.let { UUID.fromString(it) }
        val response = IssuesResponse(conversationUUID.toString(), issues = emptyList(), messages = emptyList())
        call.respond(HttpStatusCode.OK, response)
    }
}
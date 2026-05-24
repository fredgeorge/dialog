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

@Serializable
data class IssuesResponse(
    val issues: List<String>
)

fun Routing.issuesRoute() {
    post("/issues/{conversationUUID}") {
        val response = IssuesResponse(issues = emptyList())
        call.respond(HttpStatusCode.OK, response)
    }
}
/*
* Copyright (c) 2025-26 by Fred George
* @author Fred George  fredgeorge@acm.org
* Licensed under the MIT License; see LICENSE file in root.
*/

package com.nrkei.project.dialog.api.endpoints

import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.*

@Serializable
data class AnswerRequest(
    val label: String,
    val value: String
)

fun Routing.answerRoute() {
    post("/answers/{conversationId}") {
        val conversationId = call.parameters["conversationId"]?.let { UUID.fromString(it) }
        call.receive<AnswerRequest>()
        val response = IssuesResponse(conversationId.toString(), issues = emptyList(), messages = emptyList())
        call.respond(HttpStatusCode.OK, response)
     }
}

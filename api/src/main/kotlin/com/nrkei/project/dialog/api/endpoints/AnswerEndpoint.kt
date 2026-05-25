/*
* Copyright (c) 2025-26 by Fred George
* @author Fred George  fredgeorge@acm.org
* Licensed under the MIT License; see LICENSE file in root.
*/

package com.nrkei.project.dialog.api.endpoints

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class AnswerRequest(
    val label: String,
    val value: String
)

fun Routing.answerRoute() {
    post("/answers/{conversationId}/{dialogId}") {
        val conversationId = call.parameters["conversationId"]!!
        val dialogId = call.parameters["dialogId"]!!
        call.receive<AnswerRequest>()
        val response = IssuesResponse(conversationId, issues = emptyList(), messages = emptyList())
        call.respond(OK, response)
     }
}

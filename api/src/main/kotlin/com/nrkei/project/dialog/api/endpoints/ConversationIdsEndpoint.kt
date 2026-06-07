/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.api.endpoints

import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class ConversationIdsResponse(val conversationIds: List<String> = emptyList())

fun Routing.conversationIdsRoute() {
    get("/conversation-ids") {
        val response = ConversationIdsResponse()
        call.respond(OK, response)
    }
}

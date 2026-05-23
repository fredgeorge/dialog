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
import java.time.Instant

@Serializable
data class HealthResponse(
    val status: String,
    val timestamp: String
)

fun Routing.healthRoute() {
    get("/health") {
        val response = HealthResponse(
            status = "ok",
            timestamp = Instant.now().toString()
        )
        call.respond(HttpStatusCode.OK, response)
    }
}

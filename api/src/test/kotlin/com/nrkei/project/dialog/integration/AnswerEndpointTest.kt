/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.api

import com.nrkei.project.dialog.api.endpoints.AnswerRequest
import io.ktor.client.request.*
import io.ktor.http.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import io.ktor.server.testing.*

internal class AnswerEndpointTest {

     @Test
     fun `answer endpoint returns 200()` = testApplication {
        application {
            module()
         }

        val response = client.post("/answers/550e8400-e29b-41d4-a716-446655440000") {
            contentType(ContentType.Application.Json)
            setBody(AnswerRequest(label = "haveSpouse", value = "YES"))
         }

        assertEquals(HttpStatusCode.OK, response.status)
     }

     @Test
     fun `answer endpoint returns valid json body()` = testApplication {
        application {
            module()
         }

        val response = client.post("/answers/550e8400-e29b-41d4-a716-446655440000") {
            contentType(ContentType.Application.Json)
            setBody(AnswerRequest(label = "spouseName", value = "Jane"))
         }

        val bodyText = response.bodyAsText()
        assertEquals("""{"issues":[]}""", bodyText)
     }

     @Test
     fun `answer endpoint with empty label returns 200()` = testApplication {
        application {
            module()
         }

        val response = client.post("/answers/550e8400-e29b-41d4-a716-446655440000") {
            contentType(ContentType.Application.Json)
            setBody(AnswerRequest(label = "", value = "anything"))
         }

        assertEquals(HttpStatusCode.OK, response.status)
     }
}

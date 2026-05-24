package com.nrkei.project.dialog.integration

import com.nrkei.project.dialog.api.endpoints.AnswerRequest
import com.nrkei.project.dialog.api.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

internal class AnswerEndpointTest {

    @Test
    fun `answer endpoint returns 200`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/answers/550e8400-e29b-41d4-a716-446655440000") {
            contentType(ContentType.Application.Json)
            setBody(AnswerRequest(label = "haveSpouse", value = "YES"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `answer endpoint returns valid json body`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/answers/550e8400-e29b-41d4-a716-446655440000") {
            contentType(ContentType.Application.Json)
            setBody(AnswerRequest(label = "spouseName", value = "Jane"))
        }

        val bodyText = response.bodyAsText()
        assertEquals("""{"issues":[]}""", bodyText)
    }

    @Test
    fun `answer endpoint with empty label returns 200`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/answers/550e8400-e29b-41d4-a716-446655440000") {
            contentType(ContentType.Application.Json)
            setBody(AnswerRequest(label = "", value = "anything"))
        }

        assertEquals(HttpStatusCode.OK, response.status)
    }
}
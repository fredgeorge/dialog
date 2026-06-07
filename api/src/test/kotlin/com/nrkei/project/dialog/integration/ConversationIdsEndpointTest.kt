package com.nrkei.project.dialog.integration

import com.nrkei.project.dialog.api.module
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class ConversationIdsEndpointTest {

    @Test
    fun `conversation-ids endpoint returns 200`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/conversation-ids")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `conversation-ids endpoint returns valid json body`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.get("/conversation-ids")

        val bodyText = response.bodyAsText()
        assertTrue(bodyText.contains("\"conversationIds\":"))
        assertTrue(bodyText.contains("[]"))
    }

    @Test
    @Disabled
    fun `conversation-ids endpoint returns list after login`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val newId = "a1b2c3d4-e5f6-7890-abcd-ef1234567890"
        client.post("/login/$newId")

        val response = client.get("/conversation-ids")

        val bodyText = response.bodyAsText()
        assertTrue(bodyText.contains("\",$newId,"))
        assertTrue(bodyText.contains("\",$newId]"))
    }
}

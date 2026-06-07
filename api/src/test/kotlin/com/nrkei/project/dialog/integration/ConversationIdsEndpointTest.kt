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

}

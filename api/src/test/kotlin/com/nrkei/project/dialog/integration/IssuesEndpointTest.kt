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

internal class IssuesEndpointTest {

    @Test
    fun `issues endpoint returns 200`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/issues/550e8400-e29b-41d4-a716-446655440000")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `issues endpoint returns valid json body`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/issues/550e8400-e29b-41d4-a716-446655440000")

        val bodyText = response.bodyAsText()
        assertTrue(bodyText.contains("\"conversationUUID\":\"550e8400-e29b-41d4-a716-446655440000\""))
        assertTrue(bodyText.contains("\"issues\":[]"))
        assertTrue(bodyText.contains("\"messages\":[]"))
    }

    @Test
    fun `issues endpoint requires conversationUUID`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/issues/")

        assertEquals(HttpStatusCode.NotFound, response.status)
    }
}

package com.nrkei.project.dialog.integration

import com.nrkei.project.dialog.api.module
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.server.testing.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json

internal class LoginEndpointTest {

    @Test
    fun `login endpoint returns 200`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/login")

        assertEquals(HttpStatusCode.OK, response.status)
    }

    @Test
    fun `login endpoint returns valid json body`() = testApplication {
        application {
            module()
        }

        val client = createClient {
            install(ContentNegotiation) {
                json()
            }
        }

        val response = client.post("/login")

        val bodyText = response.bodyAsText()
        assertEquals("""{"issues":[]}""", bodyText)
    }
}

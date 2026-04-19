/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog

import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.util.*

// Understands rendering objects to/from JSON or Base64
// This is generalized for use in your specific project
internal object Encoding {

    // Used to register polymorphic structures
    internal val defaultIssueSerializers = SerializersModule {
        // None by default
    }

    // The default JSON with no polymorphic structures
    // Build your own Json object with your own serializersModule
    internal val defaultJson = Json {
        serializersModule = defaultIssueSerializers
        prettyPrint = false
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    internal inline fun <reified T : Any> toBase64(dto: T, json: Json = defaultJson) =
        Base64.getEncoder().encodeToString(
            toJson(dto, json).toByteArray(Charsets.UTF_8)
        ) //.also { println(it) }

    internal inline fun <reified T : Any> toJson(dto: T, json: Json = defaultJson) =
        json.encodeToString(dto) //.also { println(it) }

    internal inline fun <reified T> fromBase64(base64: String, json: Json = defaultJson): T =
        fromJson(fromBase64ToJson(base64), json)

    internal inline fun <reified T> fromJson(jsonText: String, json: Json = defaultJson): T =
        json.decodeFromString(jsonText)

    internal fun fromBase64ToJson(base64: String) =
        String(Base64.getDecoder().decode(base64), Charsets.UTF_8)
}

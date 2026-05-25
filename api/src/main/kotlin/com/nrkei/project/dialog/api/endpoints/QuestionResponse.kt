/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.api.endpoints

import kotlinx.serialization.Serializable

@Serializable
data class QuestionResponse(
    val text: String,
    val choices: List<String>?,
    val answerType: AnswerType,
    val messages: List<String> = emptyList()
)

enum class AnswerType {
    CHOICE,
    STRING,
    INTEGER,
    DOUBLE
}

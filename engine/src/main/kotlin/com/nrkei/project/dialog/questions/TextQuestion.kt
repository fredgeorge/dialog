/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.StringCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.QuestionConsequences
import com.nrkei.project.dialog.model.Result
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.SUFFICIENT
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.TOO_SHORT

// Understands a string-based answer to a Question
class TextQuestion(label: String, private val minLength: Int = 1) : Question {
    init {
        require(minLength in 1..100) { "Minimum length of $minLength is not valid for TextQuestion $label" }
    }

    val label = label(label, StringCodec)
    override val possibleResults = listOf(SUFFICIENT, TOO_SHORT)
    override val consequences = QuestionConsequences(possibleResults)
    private var result: Result? = null

    override fun answer(rawReply: Any) {
        if (rawReply !is String) {
            throw IllegalArgumentException(
                "Invalid answer type: expected String, got ${rawReply::class.simpleName} for question $label"
            )
        }
        this.result = if (rawReply.length < minLength) TOO_SHORT else SUFFICIENT
    }

    override fun consequence() = result?.let { consequences[it] }

    override fun isAnswered() = result != null

    enum class TextResult : Result { TOO_SHORT, SUFFICIENT }
}
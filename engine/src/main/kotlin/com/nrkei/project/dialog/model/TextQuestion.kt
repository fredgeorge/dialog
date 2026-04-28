/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.context.StringCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.TextQuestion.TextAnswer.SUFFICIENT
import com.nrkei.project.dialog.model.TextQuestion.TextAnswer.TOO_SHORT

// Understands a string-based answer to a Question
class TextQuestion(label: String, private val minLength: Int = 1) : Question {
    val label = label(label, StringCodec)
    override val possibleAnswers = listOf(SUFFICIENT, TOO_SHORT)
    override val consequences = mutableMapOf<Answer, Consequence>()
    override var answer: Answer? = null

    override fun answer(answer: Any) {
        require(answer is String)
        { "Invalid String answer of $answer for question $label" }
        this.answer = if (answer.length < minLength) TOO_SHORT else SUFFICIENT
    }

    enum class TextAnswer : Answer { TOO_SHORT, SUFFICIENT }
}
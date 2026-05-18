/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.Context
import com.nrkei.project.context.ContextLabel
import com.nrkei.project.context.StringCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DialogVisitor
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.SUFFICIENT
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.TOO_SHORT

// Purpose: Understands a string-based answer to a Question
class TextQuestion private constructor(
    override val label: String,
    private val minLength: Int,
    private val contextLabel: ContextLabel<String>
) : Question {

    init {
        require(minLength in 1..100) { "Minimum length of $minLength is not valid for TextQuestion $label" }
    }

    constructor(label: String, minLength: Int = 1) : this(label, minLength, label(label, StringCodec))

    override val possibleResults = listOf(SUFFICIENT, TOO_SHORT)
    private var answer: String? = null
    private var result: TextResult? = null

    override fun answer(answer: Any) {
        if (answer !is String) {
            throw IllegalArgumentException(
                "Invalid answer type: expected String, got ${answer::class.simpleName} for question $label"
            )
        }
        this.answer = answer
        this.result = if (answer.length < minLength) TOO_SHORT else SUFFICIENT
    }

    override fun result() = result

    override fun clone() = TextQuestion(label, minLength, contextLabel)

    override fun accept(visitor: DialogVisitor) {
        visitor.visit(this, label, possibleResults, result, answer)
    }

    override fun save(context: Context) {
        answer?.also { context[contextLabel] = it }
    }

    enum class TextResult : Result { TOO_SHORT, SUFFICIENT }
}
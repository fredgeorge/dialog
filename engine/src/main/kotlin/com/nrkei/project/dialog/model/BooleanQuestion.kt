/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.Companion.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus.Companion.STARTED


class BooleanQuestion(override val id: QuestionId, private val choices: Choices) : Question {
    init {
        require(choices.size == 2) { "Exactly two choices (true and false) are required for a true/false question" }
        require(choices.keys.all { it in listOf(true, false) }) { "Invalid values for a true/false question" }
    }

    constructor(idLabel: String, choices: Choices) : this(QuestionId(idLabel), choices)

    private var answer: Boolean? = null

    override fun status() = answer
        ?.let {
            val next = choices[it]
            next?.status().let { status -> if (status == NOT_STARTED) STARTED else status }
        }
        ?: NOT_STARTED

    override fun questionOrNull(id: QuestionId) =
        if (this.id == id) this else choices.values.question(id)

    override fun nextQuestion() = answer
        ?.let {
            choices[it]
                ?.nextQuestion()
                ?: throw IllegalStateException("Missing choice for true or false")
        }
        ?: this

    override fun be(value: Any) {
        if (value is Boolean) be(value) else throw IllegalArgumentException("Answer must be true or false")
    }

    override fun reset() {
        answer = null
    }

    override fun resetAll() {
        answer = null
        choices.values.forEach { it.resetAll() }
    }

    private fun be(value: Boolean) {
        answer = value
    }

    override fun accept(visitor: QuestionVisitor) {
        visitor.preVisit(this, answer, choices)
        choices.forEach {
            visitor.visit(it.key, it.value)
            it.value.accept(visitor)
        }
        visitor.postVisit(this, answer, choices)
    }

    override fun toString() = "BooleanQuestion $id"
}
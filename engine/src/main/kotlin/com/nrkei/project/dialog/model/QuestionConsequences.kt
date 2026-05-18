/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.IN_PROGRESS
import com.nrkei.project.dialog.model.DialogStatus.NOT_STARTED

// Purpose: Understands the outcome (action) for each Result of a Question
class QuestionConsequences internal constructor(
    private val question: Question,
    private val allowedResults: List<Result>
) : Consequence {
    private val consequences = mutableMapOf<Result, Consequence>()

    internal operator fun set(result: Result, consequence: Consequence) {
        require(result in allowedResults) {
            "Answer $result is not a valid choice for this question. Allowed: ${allowedResults.map { it.name }}"
        }
        require(!consequences.containsKey(result)) {
            "Consequence for answer $result has already been set."
        }
        consequences[result] = consequence
    }

    internal operator fun get(result: Result): Consequence = consequences[result]
        ?: throw IllegalStateException("Consequence for answer $result not found.")

    /**
     * Checks if all allowed answers have been assigned a consequence.
     */
    internal fun validate() =
        require(allowedResults.size == consequences.size)
        { "All possible Answers must be specified" }

    override fun status() = question.result().let { result ->
        when (result) {
            null -> NOT_STARTED
            else -> this[result].status().let { status ->
                if (status == NOT_STARTED) IN_PROGRESS else status
            }
        }
    }

    override fun nextQuestionOrNull() =
        question.result().let { result ->
            when (result) {
                null -> question
                else -> this[result].nextQuestionOrNull()
            }
        }

    override fun clone(): QuestionConsequences = QuestionConsequences(question.clone(), allowedResults)
        .also { it.consequences.putAll(consequences.mapValues { it.value.clone() }) }

    override fun accept(visitor: DialogVisitor) {
        visitor.preVisit(this, question)
        question.accept(visitor)
        consequences.forEach { result, consequence ->
            visitor.preVisit(result, consequence)
            consequence.accept(visitor)
            visitor.postVisit(result, consequence)
        }
        visitor.postVisit(this, question)
    }
}
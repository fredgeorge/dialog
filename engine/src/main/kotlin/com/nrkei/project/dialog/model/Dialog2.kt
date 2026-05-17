/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.*

// Purpose: Understands a series of questions to satisfy a need
class Dialog internal constructor(private val questions: List<Question>) : Question {
    override val possibleResults = emptyList<Result>() // n/a
    override val consequences = QuestionConsequences(possibleResults)

    override fun answer(answer: Any) {
        throw IllegalArgumentException("Only Questions can be answered; this is a Dialog")
    }

    override fun consequence() = null

    override fun isAnswered(): Boolean {
        throw IllegalArgumentException("Only Questions can be answered; this is a Dialog")
    }

    override fun nextQuestionOrNull(): Question? {
        questions.forEach { it.nextQuestionOrNull()?.also { return it } }
        return null
    }

    override fun status() = questions
        .map { it.status() }
        .let { statuses: List<DialogStatus> ->
            when {
                statuses.isEmpty() -> NOT_STARTED
                statuses.all { it == NOT_STARTED } -> NOT_STARTED
                statuses.any { it == IN_PROGRESS } -> IN_PROGRESS
                statuses.all { it == SUCCESS } -> SUCCESS
                statuses.any { it == PROBLEMS } -> PROBLEMS
                statuses.any { it == SUCCESS } -> IN_PROGRESS
                else -> PROBLEMS
            }
        }

    override fun clone() = Dialog(this.questions.map { it.clone() as Question} )
}

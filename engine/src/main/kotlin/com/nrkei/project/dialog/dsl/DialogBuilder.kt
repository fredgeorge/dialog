/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.dsl

import com.nrkei.project.dialog.model.*

// DSL syntax to specify a series of questions
infix fun DialogPurpose.dialog(block: DialogBuilder.() -> Unit): Dialog =
    DialogBuilder(this).let {
        it.block()
        it.result()
    }

// Purpose: Understands defining a sequence of questions and their consequences
class DialogBuilder internal constructor(private val purpose: DialogPurpose) {
    private val questionConsequences = mutableListOf<QuestionConsequences>()

    // Syntax sugar
    val first get() = this.also { require(questionConsequences.isEmpty()) { "'then' keyword required for each question after the first in a dialog" } }
    val then get() = this.also { require(questionConsequences.isNotEmpty()) { "'first' keyword required for the first question in a dialog" } }

    infix fun ask(question: Question) =
        QuestionBuilder(question).also { questionConsequences.add(it.result()) }

    internal fun result() = Dialog(purpose, questionConsequences)
}

class QuestionBuilder internal constructor(question: Question) {
    private val questionConsequences = QuestionConsequences(question, question.possibleResults)

    infix fun answers(block: ConsequencesBuilder.() -> Unit) =
        ConsequencesBuilder(questionConsequences).block()
            .also {
                questionConsequences.validate()
            }

    internal fun result() = questionConsequences
}

class ConsequencesBuilder internal constructor(private val questionConsequences: QuestionConsequences) {
    private lateinit var result: Result

    operator fun Result.unaryMinus() = this@ConsequencesBuilder.also { result = this }

    infix fun conclude(consequence: Consequence) {
        questionConsequences[result] = consequence
    }

    infix fun ask(innerQuestion: Question) =
        QuestionBuilder(innerQuestion).also {
            questionConsequences[result] = it.result()
        }
}

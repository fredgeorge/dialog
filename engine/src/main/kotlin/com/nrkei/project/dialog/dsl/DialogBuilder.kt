/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.dsl

import com.nrkei.project.dialog.model.Consequence
import com.nrkei.project.dialog.model.Dialog
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result

// DSL syntax to specify a series of questions
fun dialog(block: DialogBuilder.() -> Unit): Dialog =
    DialogBuilder().let {
        it.block()
        it.result()
    }

// Purpose: Understands defining a sequence of questions and their consequences
class DialogBuilder internal constructor() {
    private val questions = mutableListOf<Question>()

    // Syntax sugar
    val first get() = this.also { require(questions.isEmpty()) { "'then' keyword required for each question after the first in a dialog" } }
    val then get() = this.also { require(questions.isNotEmpty()) { "'first' keyword required for the first question in a dialog" } }

    infix fun ask(question: Question) = QuestionBuilder(question).also { questions.add(question) }

    internal fun result() = Dialog(questions)

    inner class QuestionBuilder internal constructor(private val question: Question) {

        infix fun answers(block: ConsequencesBuilder.() -> Unit) =
            this@DialogBuilder.also {
                ConsequencesBuilder(question).also { builder ->
                    builder.block()
                }
                question.consequences.validate()
            }
    }
}

class ConsequencesBuilder internal constructor(private val question: Question) {
    private lateinit var result: Result

    operator fun Result.unaryMinus() = this@ConsequencesBuilder.also { result = this }

    infix fun conclude(consequence: Consequence) {
        question.consequences[result] = consequence
    }

    infix fun ask(innerQuestion: Question) = QuestionBuilder(innerQuestion).also {
        question.consequences[result] = innerQuestion
    }

    inner class QuestionBuilder internal constructor(private val question: Question) {

        infix fun answers(block: ConsequencesBuilder.() -> Unit) = this@ConsequencesBuilder.also {
            ConsequencesBuilder(question).also { builder ->
                builder.block()
            }
            question.consequences.validate()
        }
    }
}

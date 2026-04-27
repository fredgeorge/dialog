/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.dsl

import com.nrkei.project.dialog.model.*


// DSL syntax to specify a series of questions
fun dialog2(block: Dialog2.() -> Unit) =
    Dialog2().also { it.block() }

// A series of questions to figure out a conclusion
class Dialog2 internal constructor() : Question2 {
    private val questions = mutableListOf<Question2>()

    // Syntax sugar
    val first get() = this.also { require(questions.isEmpty()) { "'then' keyword required for each question after the first in a dialog" } }
    val then get() = this.also { require(questions.isNotEmpty()) { "'first' keyword required for the first question in a dialog" } }

    infix fun ask(question: Question2) = QuestionBuilder(question)

    override fun answer(answer: Answer) {
        throw IllegalArgumentException("Only Questions can be answered; this is a Dialog")
    }

    override fun nextQuestionOrNull(): Question2? {
        questions.forEach { it.nextQuestionOrNull()?.also { return it } }
        return null
    }

//    fun status() = questions
//        .map { it.status() }
//        .let { statuses: List<DialogStatus> ->
//            when {
//                statuses.isEmpty() -> NOT_STARTED
//                statuses.all { it == NOT_STARTED } -> NOT_STARTED
//                statuses.any { it == STARTED } -> STARTED
//                statuses.all { it == SUCCEEDED } -> SUCCEEDED
//                statuses.any { it == FAILED } -> FAILED
//                statuses.any { it == SUCCEEDED } -> STARTED
//                else -> FAILED
//            }
//        }

    inner class QuestionBuilder internal constructor(private val question: Question2) {

        infix fun answers(block: AnswersBuilder2.() -> Unit): Dialog2 =
            AnswersBuilder2()
                .let { it.block() }
                .also { questions.add(question) }
                .let { this@Dialog2 }
    }
}

class AnswersBuilder2 internal constructor() {
    private val choices = mutableMapOf<Answer, Consequence>()
    private lateinit var answer: Answer


    operator fun Answer.unaryMinus() = this@AnswersBuilder2.also { answer = this }

    infix fun conclude(consequence: Consequence) {
        choices[answer] = consequence
    }

    infix fun ask(question: Question2) = QuestionBuilder(question)

    inner class QuestionBuilder internal constructor(private val question: Question2) {

        infix fun answers(block: AnswersBuilder.() -> Question2) =
            AnswersBuilder()
                .let { it.block() }
    }
}

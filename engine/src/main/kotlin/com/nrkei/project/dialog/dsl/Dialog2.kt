/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.dsl

import com.nrkei.project.dialog.model.Answer
import com.nrkei.project.dialog.model.Consequence
import com.nrkei.project.dialog.model.DialogStatus2
import com.nrkei.project.dialog.model.DialogStatus2.IN_PROGRESS
import com.nrkei.project.dialog.model.DialogStatus2.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus2.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus2.SUCCESS
import com.nrkei.project.dialog.model.Question2

// DSL syntax to specify a series of questions
fun dialog2(block: Dialog2.() -> Unit) =
    Dialog2().also { it.block() }

// Understands a series of questions to satify a need
class Dialog2 internal constructor() : Question2 {
    private val questions = mutableListOf<Question2>()
    override val possibleAnswers = emptyList<Answer>() // n/a
    override val consequences = mutableMapOf<Answer, Consequence>() // n/a

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

    override fun status() = questions
        .map { it.status() }
        .let { statuses: List<DialogStatus2> ->
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

    inner class QuestionBuilder internal constructor(private val question: Question2) {

        infix fun answers(block: ConsequencesBuilder.() -> Unit): Dialog2 =
            this@Dialog2.also {
                it.questions.add(question)
                ConsequencesBuilder(question).also { builder ->
                    builder.block()
                    require(builder.consequenceCount == question.possibleAnswers.size)
                    { "Expected ${question.possibleAnswers.size} consequences, but got ${builder.consequenceCount}" }
                }
                question.validateConsequences()
            }
    }
}

class ConsequencesBuilder internal constructor(private val question: Question2) {
    private lateinit var answer: Answer
    internal var consequenceCount = 0

    operator fun Answer.unaryMinus() = this@ConsequencesBuilder.also { answer = this }

    infix fun conclude(consequence: Consequence) {
        question.consequences[answer] = consequence.also { consequenceCount++ }
    }

    infix fun ask(innerQuestion: Question2) = QuestionBuilder(innerQuestion).also {
        question.consequences[answer] = innerQuestion.also { consequenceCount++ }
    }

    inner class QuestionBuilder internal constructor(private val question: Question2) {

        infix fun answers(block: ConsequencesBuilder.() -> Unit) = this@ConsequencesBuilder.also {
            ConsequencesBuilder(question).also { builder ->
                builder.block()
                require(builder.consequenceCount == question.possibleAnswers.size)
                { "Expected question.possibleAnswers.size consequences, but got ${builder.consequenceCount}" }
            }
            question.validateConsequences()
        }
    }
}

/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.dsl

import com.nrkei.project.dialog.model.Answer
import com.nrkei.project.dialog.model.Consequence
import com.nrkei.project.dialog.model.DialogStatus
import com.nrkei.project.dialog.model.DialogStatus.IN_PROGRESS
import com.nrkei.project.dialog.model.DialogStatus.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus.SUCCESS
import com.nrkei.project.dialog.model.Question

// DSL syntax to specify a series of questions
fun dialog(block: Dialog.() -> Unit) =
    Dialog().also { it.block() }

// Understands a series of questions to satify a need
class Dialog internal constructor() : Question {
    private val questions = mutableListOf<Question>()
    override val possibleAnswers = emptyList<Answer>() // n/a
    override val consequences = mutableMapOf<Answer, Consequence>() // n/a

    // Syntax sugar
    val first get() = this.also { require(questions.isEmpty()) { "'then' keyword required for each question after the first in a dialog" } }
    val then get() = this.also { require(questions.isNotEmpty()) { "'first' keyword required for the first question in a dialog" } }

    infix fun ask(question: Question) = QuestionBuilder(question)

    override fun answer(answer: Any) {
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

    inner class QuestionBuilder internal constructor(private val question: Question) {

        infix fun answers(block: ConsequencesBuilder.() -> Unit): Dialog =
            this@Dialog.also {
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

class ConsequencesBuilder internal constructor(private val question: Question) {
    private lateinit var answer: Answer
    internal var consequenceCount = 0

    operator fun Answer.unaryMinus() = this@ConsequencesBuilder.also { answer = this }

    infix fun conclude(consequence: Consequence) {
        question.consequences[answer] = consequence.also { consequenceCount++ }
    }

    infix fun ask(innerQuestion: Question) = QuestionBuilder(innerQuestion).also {
        question.consequences[answer] = innerQuestion.also { consequenceCount++ }
    }

    inner class QuestionBuilder internal constructor(private val question: Question) {

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

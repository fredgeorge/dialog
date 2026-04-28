/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.context.EnumCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DialogStatus.*
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.YES

// Understands a single boolean solicitation
class YesNoQuestion(label: String) : Question {
    val label = label(label, EnumCodec<YesNoChoice>(YesNoChoice::class.java))
    override val possibleAnswers: List<Answer> = listOf(YES, NO)
    override val consequences = mutableMapOf<Answer, Consequence>()
    private var answer: Answer? = null

    override fun answer(answer: Any) {
        require(answer is YesNoChoice && answer in possibleAnswers)
        { "Invalid answer of $answer for question $label" }
        this.answer = answer
    }

    override fun status(): DialogStatus {
        return consequences[answer]?.let {
            when (it.status()) {
                NOT_STARTED, IN_PROGRESS -> IN_PROGRESS
                SUCCESS -> SUCCESS
                PROBLEMS -> PROBLEMS
            }
        } ?: NOT_STARTED
    }

    override fun nextQuestionOrNull() : Question? {
        if (answer == null) return this
        return consequences[answer]?.nextQuestionOrNull()
    }

    override fun validateConsequences() {
        require(consequences.keys == possibleAnswers.toSet()) { "Must have a consequence for each possible answer" }
    }

    enum class YesNoChoice : Answer { YES, NO }
}
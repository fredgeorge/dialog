/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus2.IN_PROGRESS
import com.nrkei.project.dialog.model.DialogStatus2.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus2.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus2.SUCCESS
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.YES

// Understands a single boolean solicitation
class YesNoQuestion(label: String) : Question2 {
    val label = QuestionLabel(label)
    override val possibleAnswers: List<Answer> = listOf(YES, NO)
    override val consequences = mutableMapOf<Answer, Consequence>()
    private var answer: Answer? = null

    override fun answer(answer: Answer) {
        require(answer in possibleAnswers) { "Invalid answer of $answer for question $label" }
        this.answer = answer
    }

    override fun status(): DialogStatus2 {
        return consequences[answer]?.let {
            when (it.status()) {
                NOT_STARTED, IN_PROGRESS -> IN_PROGRESS
                SUCCESS -> SUCCESS
                PROBLEMS -> PROBLEMS
            }
        } ?: NOT_STARTED
    }

    override fun nextQuestionOrNull() : Question2? {
        if (answer == null) return this
        return consequences[answer]?.nextQuestionOrNull()
    }

    override fun validateConsequences() {
        require(consequences.keys == possibleAnswers.toSet()) { "Must have a consequence for each possible answer" }
    }

    enum class YesNoChoice : Answer { YES, NO }
}
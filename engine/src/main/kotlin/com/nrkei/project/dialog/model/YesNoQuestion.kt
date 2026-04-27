/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.YES

// Understands a single boolean solicitation
class YesNoQuestion(label: String): Question2 {
    val label = QuestionLabel(label)
    override val possibleAnswers: List<Answer> = listOf(YES, NO)
    override val consequences = mutableMapOf<Answer, Consequence>()
    private var answer: Answer? = null

    override fun answer(answer: Answer) {
        require(answer in possibleAnswers) { "Invalid answer of $answer for question $label" }
        this.answer = answer
    }

    override fun status(): DialogStatus2 {
        return consequences[answer]?.status() ?: DialogStatus2.NOT_STARTED
    }

    override fun nextQuestionOrNull() = if (answer == null) this else null

    override fun validateConsequences() {
        require(consequences.keys == possibleAnswers.toSet()) { "Must have a consequence for each possible answer" }
    }

    enum class YesNoChoice: Answer { YES, NO }
}
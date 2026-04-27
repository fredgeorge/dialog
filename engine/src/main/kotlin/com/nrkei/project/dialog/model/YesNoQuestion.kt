/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.YES

// Understands SOMETHING_DUMMY
class YesNoQuestion(label: String): Question2 {
    val label = QuestionLabel(label)
    val possibleAnswers: List<Answer> = listOf(YES, NO)
    override val consequences = mutableMapOf<Answer, Consequence>()
    private var answer: Answer? = null

    override fun answer(answer: Answer) {
        require(answer in possibleAnswers) { "Invalid answer of $answer for question $label" }
        this.answer = answer
    }

    override fun nextQuestionOrNull() = if (answer == null) this else null

    enum class YesNoChoice: Answer { YES, NO }
}
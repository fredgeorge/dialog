/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.QuestionConsequences
import com.nrkei.project.dialog.model.Result

// Purpose: Understands a single boolean solicitation
class YesNoQuestion(private val label: String) : Question {
    override val possibleResults: List<Result> = listOf(YesNoChoice.YES, YesNoChoice.NO)
    override val consequences = QuestionConsequences(possibleResults)
    private var result: Result? = null

    override fun answer(answer: Any) {
        require(answer is YesNoChoice && answer in possibleResults)
        { "Invalid answer of $answer for question $label" }
        result = answer
    }

    override fun consequence() = result?.let { consequences[it] }

    override fun isAnswered() = result != null

    override fun clone() = YesNoQuestion(label).also { question ->
        question.consequences.cloneFrom(this.consequences)
    }

    enum class YesNoChoice : Result { YES, NO }
}
/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.EnumCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.Answer
import com.nrkei.project.dialog.model.Consequence
import com.nrkei.project.dialog.model.Question

// Understands a single boolean solicitation
class YesNoQuestion(label: String) : Question {

    val label = label(label, EnumCodec<YesNoChoice>(YesNoChoice::class.java))
    override val possibleAnswers: List<Answer> = listOf(YesNoChoice.YES, YesNoChoice.NO)
    override val consequences = mutableMapOf<Answer, Consequence>()
    private var answer: Answer? = null

    override fun answer(answer: Any) {
        require(answer is YesNoChoice && answer in possibleAnswers)
        { "Invalid answer of $answer for question $label" }
        this.answer = answer
    }

    override fun consequence() = answer?.let { consequences[it] }

    override fun isAnswered() = answer != null

    enum class YesNoChoice : Answer { YES, NO }
}
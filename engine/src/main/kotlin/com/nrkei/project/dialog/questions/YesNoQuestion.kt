/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.EnumCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.Answer
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.QuestionConsequences

// Understands a single boolean solicitation
class YesNoQuestion(label: String) : Question {

    val label = label(label, EnumCodec<YesNoChoice>(YesNoChoice::class.java))
    override val possibleAnswers: List<Answer> = listOf(YesNoChoice.YES, YesNoChoice.NO)
    override val consequences = QuestionConsequences(possibleAnswers)
    private var answer: Answer? = null

    override fun answer(rawReply: Any) {
        require(rawReply is YesNoChoice && rawReply in possibleAnswers)
        { "Invalid answer of $rawReply for question $label" }
        answer = rawReply
    }

    override fun consequence() = answer?.let { consequences[it] }

    override fun isAnswered() = answer != null

    enum class YesNoChoice : Answer { YES, NO }
}
/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.IntCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.Answer
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.QuestionConsequences
import com.nrkei.project.dialog.questions.IntRangeQuestion.IntRangeAnswer
import kotlin.reflect.KClass

// Understands a Question with Answer ranges
class IntRangeQuestion<R>(label: String, valuesEnum: KClass<R>) : Question
        where R : Enum<R>, R : IntRangeAnswer {

    companion object {
        fun positiveInt(label: String) =
            IntRangeQuestion<PositiveIntRange>(label, PositiveIntRange::class)

        fun zeroOrMoreInt(label: String) =
            IntRangeQuestion<NonNegativeIntRange>(label, NonNegativeIntRange::class)
    }

    val label = label(label, IntCodec)
    override val possibleAnswers: List<Answer> = valuesEnum.java.enumConstants.toList()
    override val consequences = QuestionConsequences(possibleAnswers)
    private var answer: Answer? = null

    override fun answer(rawReply: Any) {
        require(rawReply is Int)
        { "Invalid answer of $rawReply for question $label" }
        this.answer = possibleAnswers.first { (it as IntRangeAnswer).inRange(rawReply) }
    }

    override fun consequence() = answer?.let { consequences[it] }

    override fun isAnswered() = answer != null

    interface IntRangeAnswer : Answer {
        val minimum: Int
        val maximum: Int
        fun inRange(value: Int) = value in minimum..maximum
    }

    enum class NonNegativeIntRange(override val minimum: Int, override val maximum: Int) : IntRangeAnswer {
        INVALID(Int.MIN_VALUE, -1),
        VALID(0, Int.MAX_VALUE)
    }

    enum class PositiveIntRange(override val minimum: Int, override val maximum: Int) : IntRangeAnswer {
        INVALID(Int.MIN_VALUE, 0),
        VALID(1, Int.MAX_VALUE)
    }
}
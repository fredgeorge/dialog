/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.IntCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.Answer
import com.nrkei.project.dialog.model.Consequence
import com.nrkei.project.dialog.questions.IntRangeQuestion.IntRangeAnswer
import com.nrkei.project.dialog.model.Question
import kotlin.reflect.KClass

// Understands a Question with Answer ranges
class IntRangeQuestion<R>(label: String, valuesEnum: KClass<R>) : Question
        where R : Enum<R>, R : IntRangeAnswer {

            companion object {
                fun positiveInt(label: String) =
                    IntRangeQuestion<PositiveRange>(label, PositiveRange::class)
                fun zeroOrMoreInt(label: String) =
                    IntRangeQuestion<NonNegativeRange>(label, NonNegativeRange::class)
            }

    val label = label(label, IntCodec)
    override val possibleAnswers: List<Answer> = valuesEnum.java.enumConstants.toList()
    override val consequences = mutableMapOf<Answer, Consequence>()
    override var answer: Answer? = null

    override fun answer(answer: Any) {
        require(answer is Int)
        { "Invalid answer of $answer for question $label" }
        this.answer = possibleAnswers.first { (it as IntRangeAnswer).inRange(answer) }
    }

    interface IntRangeAnswer : Answer {
        val minimum: Int
        val maximum: Int
        fun inRange(value: Int) = value in minimum..maximum
    }

    enum class NonNegativeRange(override val minimum: Int, override val maximum: Int): IntRangeAnswer {
        INVALID(Int.MIN_VALUE, -1),
        VALID(0, Int.MAX_VALUE)
    }

    enum class PositiveRange(override val minimum: Int, override val maximum: Int): IntRangeAnswer {
        INVALID(Int.MIN_VALUE, 0),
        VALID(1, Int.MAX_VALUE)
    }
}
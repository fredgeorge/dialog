/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.DoubleCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.Answer
import com.nrkei.project.dialog.model.Consequence
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.questions.DoubleRangeQuestion.DoubleRangeAnswer
import kotlin.reflect.KClass

// Understands a Question with Answer ranges expressed as Double values
class DoubleRangeQuestion<R>(label: String, valuesEnum: KClass<R>) : Question
        where R : Enum<R>, R : DoubleRangeAnswer {

    companion object {
        fun positiveDouble(label: String) =
            DoubleRangeQuestion<PositiveDoubleRange>(label, PositiveDoubleRange::class)

        fun zeroOrMoreDouble(label: String) =
            DoubleRangeQuestion<NonNegativeDoubleRange>(label, NonNegativeDoubleRange::class)
    }

    val label = label(label, DoubleCodec)
    override val possibleAnswers: List<Answer> = valuesEnum.java.enumConstants.toList()
    override val consequences = mutableMapOf<Answer, Consequence>()
    private var answer: Answer? = null

    override fun answer(answer: Any) {
        require(answer is Number)
        { "Invalid answer of $answer for question $label" }
        this.answer = possibleAnswers
            .first { (it as DoubleRangeAnswer).inRange(answer.toDouble()) }
    }

    override fun consequence() = answer?.let { consequences[it] }

    override fun isAnswered() = answer != null

    interface DoubleRangeAnswer : Answer {
        val minimum: Double
        val maximum: Double
        fun inRange(value: Double) = value >= minimum && value < maximum
    }

    enum class NonNegativeDoubleRange(override val minimum: Double, override val maximum: Double) : DoubleRangeAnswer {
        INVALID(-Double.MAX_VALUE, 0.0),
        VALID(0.0, Double.MAX_VALUE)
    }

    enum class PositiveDoubleRange(override val minimum: Double, override val maximum: Double) : DoubleRangeAnswer {
        INVALID(-Double.MAX_VALUE, 0.0),
        VALID(0.0, Double.MAX_VALUE);
        override fun inRange(value: Double) = value > minimum && value <= maximum
    }
}

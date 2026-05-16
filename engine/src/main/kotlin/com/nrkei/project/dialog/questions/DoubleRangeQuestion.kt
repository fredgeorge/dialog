/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.DoubleCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.QuestionConsequences
import com.nrkei.project.dialog.model.Result
import com.nrkei.project.dialog.questions.DoubleRangeQuestion.DoubleRangeResult
import kotlin.reflect.KClass

// Understands a Question with Answer ranges expressed as Double values
class DoubleRangeQuestion<R>(label: String, valuesEnum: KClass<R>) : Question
        where R : Enum<R>, R : DoubleRangeResult {

    companion object {
        fun positiveDouble(label: String) =
            DoubleRangeQuestion<PositiveDoubleRange>(label, PositiveDoubleRange::class)

        fun zeroOrMoreDouble(label: String) =
            DoubleRangeQuestion<NonNegativeDoubleRange>(label, NonNegativeDoubleRange::class)
    }

    val label = label(label, DoubleCodec)
    override val possibleResults: List<Result> = valuesEnum.java.enumConstants.toList()
    override val consequences = QuestionConsequences(possibleResults)
    private var result: Result? = null

    override fun answer(rawReply: Any) {
        require(rawReply is Number)
        { "Invalid answer of $rawReply for question $label" }
        this.result = possibleResults
            .first { (it as DoubleRangeResult).inRange(rawReply.toDouble()) }
    }

    override fun consequence() = result?.let { consequences[it] }

    override fun isAnswered() = result != null

    interface DoubleRangeResult : Result {
        val minimum: Double
        val maximum: Double
        fun inRange(value: Double) = value >= minimum && value < maximum
    }

    enum class NonNegativeDoubleRange(override val minimum: Double, override val maximum: Double) : DoubleRangeResult {
        INVALID(-Double.MAX_VALUE, 0.0),
        VALID(0.0, Double.MAX_VALUE)
    }

    enum class PositiveDoubleRange(override val minimum: Double, override val maximum: Double) : DoubleRangeResult {
        INVALID(-Double.MAX_VALUE, 0.0),
        VALID(0.0, Double.MAX_VALUE);
        override fun inRange(value: Double) = value > minimum && value <= maximum
    }
}

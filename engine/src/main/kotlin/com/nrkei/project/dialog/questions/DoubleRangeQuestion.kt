/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.Context
import com.nrkei.project.context.ContextLabel
import com.nrkei.project.context.DoubleCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DialogVisitor
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result
import com.nrkei.project.dialog.questions.DoubleRangeQuestion.DoubleRangeResult
import kotlin.reflect.KClass

// Purpose: Understands a Question with Answer ranges expressed as Double values
class DoubleRangeQuestion<R>(
    override val label: String,
    private val valuesEnum: KClass<R>,
    private val contextLabel: ContextLabel<Double> = label(label, DoubleCodec)
) : Question
        where R : Enum<R>, R : DoubleRangeResult {

    companion object {
        fun positiveDouble(label: String) =
            DoubleRangeQuestion(label, PositiveDoubleRange::class)

        fun zeroOrMoreDouble(label: String) =
            DoubleRangeQuestion(label, NonNegativeDoubleRange::class)
    }

    constructor(label: String, valuesEnum: KClass<R>)
            : this(label, valuesEnum, label(label, DoubleCodec))

    override val possibleResults: List<DoubleRangeResult> = valuesEnum.java.enumConstants.toList()
    private var answer: Double? = null
    private var result: Result? = null

    override fun answer(answer: Any) {
        require(answer is Number)
        { "Invalid answer of $answer for question $label" }
        this.answer = answer.toDouble()
        this.result = possibleResults.first { it.inRange(answer.toDouble()) }
    }

    override fun result() = result

    override fun clone() = DoubleRangeQuestion(label, valuesEnum, contextLabel)


    override fun accept(visitor: DialogVisitor) {
        visitor.visit(this, label, possibleResults, result, answer)
    }

    override fun save(context: Context) {
        answer?.also { context[contextLabel] = it }
    }

    interface DoubleRangeResult : Result {
        val minimum: Double
        val maximum: Double
        fun inRange(value: Double) = value in minimum..<maximum
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

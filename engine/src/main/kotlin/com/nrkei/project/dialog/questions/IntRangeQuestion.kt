/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.context.Context
import com.nrkei.project.context.ContextLabel
import com.nrkei.project.context.IntCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DialogVisitor
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result
import com.nrkei.project.dialog.questions.IntRangeQuestion.IntRangeResult
import kotlin.reflect.KClass

// Purpose: Understands a Question with Answer integer ranges
class IntRangeQuestion<R> private constructor(
    override val label: String,
    private val valuesEnum: KClass<R>,
    private val contextLabel: ContextLabel<Int>
) : Question
        where R : Enum<R>, R : IntRangeResult {

    companion object {
        fun positiveInt(label: String) =
            IntRangeQuestion(label, PositiveIntRange::class)

        fun zeroOrMoreInt(label: String) =
            IntRangeQuestion(label, NonNegativeIntRange::class)
    }

    constructor(label: String, valuesEnum: KClass<R>)
            : this(label, valuesEnum, label(label, IntCodec))

    override val possibleResults: List<IntRangeResult> = valuesEnum.java.enumConstants.toList()
    private var answer: Int? = null
    private var result: IntRangeResult? = null

    override fun answer(answer: Any) {
        require(answer is Int)
        { "Invalid answer of $answer for question $label" }
        this.answer = answer
        this.result = possibleResults.first { it.inRange(answer) }
    }

    override fun result() = result

    override fun clone() = IntRangeQuestion(label, valuesEnum, contextLabel)

    override fun accept(visitor: DialogVisitor) {
        visitor.visit(this, label, possibleResults, result, answer)
    }

    override fun save(context: Context) {
        answer?.also { context[contextLabel] = it }
    }

    interface IntRangeResult : Result {
        val minimum: Int
        val maximum: Int
        fun inRange(value: Int) = value in minimum..maximum
    }

    enum class NonNegativeIntRange(override val minimum: Int, override val maximum: Int) : IntRangeResult {
        INVALID(Int.MIN_VALUE, -1),
        VALID(0, Int.MAX_VALUE)
    }

    enum class PositiveIntRange(override val minimum: Int, override val maximum: Int) : IntRangeResult {
        INVALID(Int.MIN_VALUE, 0),
        VALID(1, Int.MAX_VALUE)
    }
}
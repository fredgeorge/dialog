/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result
import com.nrkei.project.dialog.questions.IntRangeQuestion.IntRangeResult
import kotlin.reflect.KClass

// Purpose: Understands a Question with Answer integer ranges
class IntRangeQuestion<R>(private val label: String, valuesEnum: KClass<R>) : Question
        where R : Enum<R>, R : IntRangeResult {

    companion object {
        fun positiveInt(label: String) =
            IntRangeQuestion(label, PositiveIntRange::class)

        fun zeroOrMoreInt(label: String) =
            IntRangeQuestion(label, NonNegativeIntRange::class)
    }

    override val possibleResults: List<IntRangeResult> = valuesEnum.java.enumConstants.toList()
    private var result: IntRangeResult? = null

    override fun answer(answer: Any) {
        require(answer is Int)
        { "Invalid answer of $answer for question $label" }
        this.result = possibleResults.first { it.inRange(answer) }
    }

    override fun result() = result

    override fun clone() = YesNoQuestion(label)

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
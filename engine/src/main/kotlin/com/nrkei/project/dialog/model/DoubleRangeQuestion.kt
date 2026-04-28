/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.context.DoubleCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DoubleRangeQuestion.DoubleRangeAnswer
import kotlin.reflect.KClass

// Understands a Question with Answer ranges expressed as Double values
class DoubleRangeQuestion<R>(label: String, valuesEnum: KClass<R>) : Question
        where R : Enum<R>, R : DoubleRangeAnswer {

    val label = label(label, DoubleCodec)
    override val possibleAnswers: List<Answer> = valuesEnum.java.enumConstants.toList()
    override val consequences = mutableMapOf<Answer, Consequence>()
    override var answer: Answer? = null

    override fun answer(answer: Any) {
        require(answer is Number)
        { "Invalid answer of $answer for question $label" }
        this.answer = possibleAnswers
            .first { (it as DoubleRangeAnswer).inRange(answer.toDouble()) }
    }

    interface DoubleRangeAnswer : Answer {
        val minimum: Double
        val maximum: Double
        fun inRange(value: Double) = value >= minimum && value < maximum
    }
}

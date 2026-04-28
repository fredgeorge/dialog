/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.context.IntCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DialogStatus.IN_PROGRESS
import com.nrkei.project.dialog.model.DialogStatus.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus.SUCCESS
import com.nrkei.project.dialog.model.IntRangeQuestion.IntRangeAnswer
import kotlin.reflect.KClass

// Understands a Question with Answer ranges
class IntRangeQuestion<R>(label: String, valuesEnum: KClass<R>) : Question
        where R : Enum<R>, R : IntRangeAnswer {

    val label = label(label, IntCodec)
    override val possibleAnswers: List<Answer> = valuesEnum.java.enumConstants.toList()
    override val consequences = mutableMapOf<Answer, Consequence>()
    private var answer: Answer? = null

    override fun answer(answer: Any) {
        require(answer is Int) { "Invalid answer of $answer for question $label" }
        this.answer = possibleAnswers.first { (it as IntRangeAnswer).inRange(answer) }
    }

    override fun status(): DialogStatus {
        return consequences[answer]?.let {
            when (it.status()) {
                NOT_STARTED, IN_PROGRESS -> IN_PROGRESS
                SUCCESS -> SUCCESS
                PROBLEMS -> PROBLEMS
            }
        } ?: NOT_STARTED
    }

    override fun nextQuestionOrNull() : Question? {
        if (answer == null) return this
        return consequences[answer]?.nextQuestionOrNull()
    }

    interface IntRangeAnswer : Answer {
        val minimum: Int
        val maximum: Int
        fun inRange(value: Int) = value in minimum..maximum
    }
}
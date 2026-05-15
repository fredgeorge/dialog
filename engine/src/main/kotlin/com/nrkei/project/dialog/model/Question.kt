/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.IN_PROGRESS
import com.nrkei.project.dialog.model.DialogStatus.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus.SUCCESS

// Understands solicitation of information
interface Question : Consequence {
    val possibleAnswers: List<Answer>
    val consequences: MutableMap<Answer, Consequence>

    fun answer(answer: Any)

    fun consequence(): Consequence?

    fun isAnswered(): Boolean

    fun validateConsequences() {
        require(consequences.keys == possibleAnswers.toSet())
        { "Must have a consequence for each possible answer" }
    }

    override fun status(): DialogStatus {
        return consequence()?.let {
            when (it.status()) {
                NOT_STARTED, IN_PROGRESS -> IN_PROGRESS
                SUCCESS -> SUCCESS
                PROBLEMS -> PROBLEMS
            }
        } ?: NOT_STARTED
    }

    override fun nextQuestionOrNull() : Question? {
        if (!isAnswered()) return this
        return consequence()?.nextQuestionOrNull()
    }
}

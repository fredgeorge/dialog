/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.*
import com.nrkei.project.dialog.visitors.QuestionFinder

// Purpose: Understands a series of questions to satisfy a need
class Dialog internal constructor(private val questionConsequences: List<QuestionConsequences>, purpose: String = "<unspecified>") : Consequence{
    var purpose: String = purpose
        internal set

    override fun nextQuestionOrNull(): Question? {
        questionConsequences.forEach { consequence -> consequence.nextQuestionOrNull()?.also { return it } }
        return null
    }

    override fun status() = questionConsequences
        .map { it.status() }
        .let { statuses: List<DialogStatus> ->
            when {
                statuses.isEmpty() -> NOT_STARTED
                statuses.all { it == NOT_STARTED } -> NOT_STARTED
                statuses.any { it == IN_PROGRESS } -> IN_PROGRESS
                statuses.all { it == SUCCESS } -> SUCCESS
                statuses.any { it == PROBLEMS } -> PROBLEMS
                statuses.any { it == SUCCESS } -> IN_PROGRESS
                else -> PROBLEMS
            }
        }

    override fun clone() = Dialog(this.questionConsequences.map { it.clone()}, this@Dialog.purpose)

    override fun accept(visitor: DialogVisitor) {
        visitor.preVisit(this, questionConsequences)
        questionConsequences.forEach { it.accept(visitor) }
        visitor.postVisit(this, questionConsequences)
    }

    fun question(label: String) = QuestionFinder(this, label).result()
        ?: throw IllegalArgumentException("Question with label '$label' not found in dialog")
}

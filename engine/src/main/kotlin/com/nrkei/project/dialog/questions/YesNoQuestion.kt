/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.questions

import com.nrkei.project.dialog.model.DialogVisitor
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result

// Purpose: Understands a single boolean solicitation
class YesNoQuestion(private val label: String) : Question {
    override val possibleResults: List<Result> = listOf(YesNoChoice.YES, YesNoChoice.NO)
    private var result: YesNoChoice? = null

    override fun answer(answer: Any) {
        require(answer is YesNoChoice && answer in possibleResults)
        { "Invalid answer of $answer for question $label" }
        this.result = answer
    }

    override fun result() = result

    override fun clone() = YesNoQuestion(label)

    override fun toString() = "${this.javaClass.simpleName} asking $label"

    override fun accept(visitor: DialogVisitor) {
        visitor.visit(this, label, possibleResults, result, result)
    }

    enum class YesNoChoice : Result { YES, NO }
}
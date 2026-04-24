/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.dsl

import com.nrkei.project.dialog.model.*

class AnswerSetBuilder(dialog: Dialog) : QuestionVisitor {
    val results = mutableMapOf<QuestionId, Any>()

    init {
        dialog.accept(this)
    }

    fun result() = AnswerSet(results)

    override fun postVisit(question: Question, answer: Any?, choices: Choices) {
        answer?.also { results[question.id] = it }
    }

}

/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.visitors

import com.nrkei.project.dialog.model.Dialog
import com.nrkei.project.dialog.model.DialogVisitor
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result

// Purpose: Locates a specific Question in a Dialog
internal class QuestionFinder(dialog: Dialog, private val label: String) : DialogVisitor {
    private var question: Question? = null

    init {
        dialog.accept(this)
    }

    fun result(): Question? = question

    override fun visit(
        question: Question,
        label: String,
        possibleResults: List<Result>,
        result: Result?,
        value: Any?
    ) {
        if (this.label == label) this.question = question
    }
}
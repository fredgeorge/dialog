/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.visitors

import com.nrkei.project.context.Context
import com.nrkei.project.dialog.model.Dialog
import com.nrkei.project.dialog.model.DialogVisitor
import com.nrkei.project.dialog.model.Question
import com.nrkei.project.dialog.model.Result

// Purpose: Understands fetching answers from dialog
class ExtractValues(dialog: Dialog, private val context: Context): DialogVisitor {
    init {
        dialog.accept(this)
    }

    override fun visit(question: Question, label: String, possibleResults: List<Result>, result: Result?, value: Any?) {
        question.save(context)
    }
}
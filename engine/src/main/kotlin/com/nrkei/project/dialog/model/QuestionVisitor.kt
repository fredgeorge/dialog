/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.dsl.Dialog
import com.nrkei.project.dialog.model.DialogStatus.DialogConclusion

interface QuestionVisitor {
    fun preVisit(dialog: Dialog, question: List<Question>) { }
    fun postVisit(dialog: Dialog, question: List<Question>) { }
    fun preVisit(question: Question, answer: Any?, choices: Choices) { }
    fun postVisit(question: Question, answer: Any?, choices: Choices) { }
    fun visit(value: Any, result: Question) { } // A Choice
    fun visit(conclusion: DialogConclusion) { }
}
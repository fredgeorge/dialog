/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.dsl.DialogPurpose

// Purpose: Understands a Dialog hierarchy
interface DialogVisitor {
    fun preVisit(dialog: Dialog, questionConsequences: List<QuestionConsequences>) { }
    fun postVisit(dialog: Dialog, questionConsequences: List<QuestionConsequences>) { }
    fun preVisit(questionConsequences: QuestionConsequences, question: Question) { }
    fun postVisit(questionConsequences: QuestionConsequences, question: Question) { }
    fun preVisit(result: Result, consequence: Consequence) { }
    fun postVisit(result: Result, consequence: Consequence) { }
    fun visit(
        question: Question,
        label: String,
        possibleResults: List<Result>,
        result: Result?,
        value: Any?
    ) { }
    fun visit(acceptable: Acceptable) { }
    fun visit(issue: MissingIssue, reason: String) { }
    fun visit(
        issue: ProblemIssue,
        purpose: DialogPurpose,
        question: Question,
        result: Result,
        reason: String
    ) { }
}
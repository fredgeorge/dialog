/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.dsl.DialogPurpose
import com.nrkei.project.dialog.model.*
import com.nrkei.project.template.util.OtherIncomeDialog.otherIncomeDialog
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Purpose: Ensures DialogVisitor is functioning correctly
internal class VisitorTest {

    @Test
    fun `All nodes visited`() {
        CountingVisitor(otherIncomeDialog).also { visitor ->
            assertEquals(1, visitor.dialogCount)
            assertEquals(3, visitor.dialogQuastionCount)
            assertEquals(5, visitor.questionCount)
            assertEquals(3, visitor.acceptableCount)
            assertEquals(4, visitor.missingIssueCount)
            assertEquals(1, visitor.problemIssueCount)
            assertEquals(10, visitor.resultCount)
        }
    }

    private class CountingVisitor(dialog: Dialog) : DialogVisitor {
        init {
            dialog.accept(this)
        }
        var dialogCount = 0
        var dialogQuastionCount = 0
        var questionCount = 0
        var acceptableCount = 0
        var missingIssueCount = 0
        var problemIssueCount = 0
        var resultCount = 0

        override fun preVisit(dialog: Dialog, questionConsequences: List<QuestionConsequences>) {
            dialogCount++
            dialogQuastionCount += questionConsequences.size
        }

        override fun preVisit(result: Result, consequence: Consequence) {
            resultCount++
        }

        override fun visit(
            question: Question,
            label: String,
            possibleResults: List<Result>,
            result: Result?,
            value: Any?
        ) {
            questionCount++
        }

        override fun visit(acceptable: Acceptable) {
            acceptableCount++
        }

        override fun visit(issue: MissingIssue, reason: String) {
            missingIssueCount++
        }

        override fun visit(
            issue: ProblemIssue,
            purpose: DialogPurpose,
            question: Question,
            result: Result,
            reason: String
        ) {
            problemIssueCount++
        }
    }
}
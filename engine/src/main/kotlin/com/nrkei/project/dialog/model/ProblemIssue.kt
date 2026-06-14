/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.dsl.DialogPurpose
import com.nrkei.project.issue.Issue
import com.nrkei.project.issue.IssueDto
import com.nrkei.project.issue.IssueType
import kotlinx.serialization.Serializable

// Purpose: Understands that a problem has arisen that inhibits successful resolution
class ProblemIssue(
    private val purpose: DialogPurpose,
    private val question: Question,
    private val result: Result,
    private val reason: String
):
    Consequence,
    Issue<ProblemIssue>(Consequence.dialogEngine, State.OPEN){

    companion object RejectionIssueType : IssueType<ProblemIssue>

    override val issueType = RejectionIssueType

    override fun status() = DialogStatus.PROBLEMS

    override fun nextQuestionOrNull() = null

    override fun clone() = this

    override fun accept(visitor: DialogVisitor) = visitor.visit(this, purpose, question, result, reason)

    override fun equals(other: Any?) =
        this === other || other is ProblemIssue && this.equals(other)

    private fun equals(other: ProblemIssue) =
        this.purpose == other.purpose &&
                this.question == other.question &&
                this.result == other.result &&
                this.reason == other.reason

    override fun hashCode() = reason.hashCode()

    @Suppress("UNCHECKED_CAST")
    override fun <I : Issue<I>> toDto() =
        ProblemIssueDto(description = reason) as IssueDto<I>

    override fun toString() = "Rejection issue raised: $reason"

    @Serializable
    data class ProblemIssueDto(
        override val raisedBy: String = Consequence.dialogEngine.name,
        override val state: State = State.OPEN,
        override val closedBy: String? = null,
        val description: String
    ) : IssueDto<ProblemIssue>
}
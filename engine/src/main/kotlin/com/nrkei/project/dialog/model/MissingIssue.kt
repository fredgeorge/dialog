/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.issue.Issue
import com.nrkei.project.issue.IssueDto
import com.nrkei.project.issue.IssueType
import kotlinx.serialization.Serializable

// Purpose: Understands that more information is required to reach successful resolution
class MissingIssue(val reason: String):
    Consequence,
    Issue<MissingIssue>(Consequence.dialogEngine, State.OPEN){

    companion object MissingIssueType : IssueType<MissingIssue>

    override val issueType = MissingIssueType

    override fun status() = DialogStatus.PROBLEMS

    override fun nextQuestionOrNull() = null

    override fun clone() = this

    override fun accept(visitor: DialogVisitor) = visitor.visit(this, reason)

    override fun equals(other: Any?) =
        super.equals(other) && other is MissingIssue && this.reason == other.reason

    override fun hashCode() = reason.hashCode()

    @Suppress("UNCHECKED_CAST")
    override fun <I : Issue<I>> toDto() =
        MissingIssueDto(description = reason) as IssueDto<I>

    override fun toString() = "Missing information issue raised: $reason"

    @Serializable
    data class MissingIssueDto(
        override val raisedBy: String = Consequence.dialogEngine.name,
        override val state: State = State.OPEN,
        override val closedBy: String? = null,
        val description: String
    ) : IssueDto<MissingIssue>
}
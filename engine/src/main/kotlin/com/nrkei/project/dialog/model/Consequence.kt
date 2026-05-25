/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.Consequence.Companion.dialogEngine
import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus.SUCCESS
import com.nrkei.project.issue.Issue
import com.nrkei.project.issue.Issue.State.OPEN
import com.nrkei.project.issue.IssueDto
import com.nrkei.project.issue.IssueParty
import com.nrkei.project.issue.IssueType
import kotlinx.serialization.Serializable

// Purpose: Understands next action (or no next action) for a Result of a Question
sealed interface Consequence {
    companion object {
        internal val dialogEngine = IssueParty("Dialog Engine")
    }
    fun status(): DialogStatus
    fun nextQuestionOrNull(): Question?
    fun clone(): Consequence
    fun accept(visitor: DialogVisitor)
}

// Purpose: Understands that no further action is required
object Acceptable: Consequence {
    override fun status() = SUCCESS
    override fun clone() = this
    override fun nextQuestionOrNull() = null
    override fun accept(visitor: DialogVisitor) = visitor.visit(this)
}

// Purpose: Understands that a problem has arisen that inhibits successful resolution
class RejectionIssue(private val reason: String):
    Consequence,
    Issue<RejectionIssue>(dialogEngine, OPEN){

    companion object RejectionIssueType : IssueType<RejectionIssue>

    override val issueType = RejectionIssueType

    override fun status() = PROBLEMS

    override fun nextQuestionOrNull() = null

    override fun clone() = this

    override fun accept(visitor: DialogVisitor) = visitor.visit(this, reason)

    @Suppress("UNCHECKED_CAST")
    override fun <I : Issue<I>> toDto() =
        RejectionIssueDto(description = reason) as IssueDto<I>

    override fun toString() = "Rejection issue raised: $reason"

    @Serializable
    data class RejectionIssueDto(
        override val raisedBy: String = dialogEngine.name,
        override val state: State = OPEN,
        override val closedBy: String? = null,
        val description: String
    ) : IssueDto<RejectionIssue>
}

// Purpose: Understands that more information is required to reach successful resolution
class MissingIssue(val reason: String):
    Consequence,
    Issue<MissingIssue>(dialogEngine, OPEN){

    companion object MissingIssueType : IssueType<MissingIssue>

    override val issueType = MissingIssueType

    override fun status() = PROBLEMS

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
        override val raisedBy: String = dialogEngine.name,
        override val state: State = OPEN,
        override val closedBy: String? = null,
        val description: String
    ) : IssueDto<MissingIssue>
}

fun problem(message: String) = RejectionIssue(message)

fun missing(message: String) = MissingIssue(message)

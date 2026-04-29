/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus.SUCCESS
import com.nrkei.project.issue.Issue
import com.nrkei.project.issue.IssueParty
import com.nrkei.project.issue.Issue.State.OPEN
import com.nrkei.project.issue.IssueDto
import com.nrkei.project.issue.IssueType

// Understands next action (or no next action) for an Answer
interface Consequence {
    fun status(): DialogStatus
    fun nextQuestionOrNull(): Question? = null
}

object Acceptable: Consequence {
    override fun status() = SUCCESS
}

class RejectionIssue(private val reason: String):
    Consequence,
    Issue<RejectionIssue>(IssueParty("Dialog Engine"), OPEN){

    companion object RejectionIssueType : IssueType<RejectionIssue>

    override val issueType = RejectionIssueType

    override fun status() = PROBLEMS

    override fun <I : Issue<I>> toDto(): IssueDto<I> {
        TODO("Not yet implemented")
    }

    override fun toString() = "Issue raised: $reason"
}

fun problem(message: String) = RejectionIssue(message)

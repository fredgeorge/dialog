/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.SUCCESS
import com.nrkei.project.issue.IssueParty

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

fun missing(message: String) = MissingIssue(message)

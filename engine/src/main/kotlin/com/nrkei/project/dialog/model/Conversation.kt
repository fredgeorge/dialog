/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.issue.Issue
import com.nrkei.project.issue.Issue.State.OPEN
import java.util.*

// Purpose: Understands Dialogs that should be invoked in a particular circumstance
class Conversation private constructor(
    private val dialogs: Map<MissingIssue, Dialog>,
    private val openIssues: MutableList<Issue<*>> = mutableListOf(),
    @Suppress("unused") val conversationId: UUID = UUID.randomUUID()
) {

    constructor(firstDialog: Pair<MissingIssue, Dialog>, vararg dialogs: Pair<MissingIssue, Dialog>) :
            this(listOf(firstDialog, *dialogs).toMap(), listOf(firstDialog, *dialogs).map { it.first }.toMutableList())

    operator fun get(issue: MissingIssue) = dialogs[issue] ?: throw IllegalStateException("No Dialog defined for issue $issue")

    fun clone() = Conversation(
        dialogs.mapValues { (_, value) -> value.clone() }.toMap(),
        openIssues.toMutableList()
    )

    fun openIssues() = openIssues.filter { issue ->
        issue.state() == OPEN &&
        dialogs[issue]?.let { it.nextQuestionOrNull() != null } ?: false
    }
}
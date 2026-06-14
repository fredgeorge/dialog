/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.context.ContextCodec.ContextDto
import com.nrkei.project.dialog.dsl.DialogPurpose
import com.nrkei.project.dialog.model.Dialog.Companion.findDialogByPurpose
import com.nrkei.project.issue.Issue.State.OPEN
import java.util.*

// Purpose: Understands Dialogs that should be invoked in a particular circumstance
class Conversation private constructor(
    private val dialogs: MutableMap<MissingIssue, Dialog>,
    @Suppress("unused") val conversationId: UUID = UUID.randomUUID()
) {

    constructor(firstDialog: Pair<MissingIssue, Dialog>, vararg dialogs: Pair<MissingIssue, Dialog>) :
            this(listOf(firstDialog, *dialogs).toMap().toMutableMap())

    operator fun get(issue: MissingIssue) = dialogs[issue] ?: throw IllegalStateException("No Dialog defined for issue $issue")

    operator fun get(purpose: DialogPurpose) = dialogs.values.findDialogByPurpose(purpose)

    fun clone() = Conversation(
        dialogs.mapValues { (_, value) -> value.clone() }.toMap().toMutableMap()
    )

    fun openIssues() = dialogs.keys.filter { issue ->
        issue.state() == OPEN &&
        dialogs[issue]?.let { it.nextQuestionOrNull() != null } ?: false
    }

    data class ConversationDto(
        val conversationId: UUID,
        val missingIssueReasons: List<String>,
        val rejectionIssueReasons: List<String>,
        val contextDto: ContextDto
    )
}
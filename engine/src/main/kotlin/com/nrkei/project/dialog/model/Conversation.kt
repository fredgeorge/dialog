/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.context.ContextCodec.ContextDto
import com.nrkei.project.dialog.dsl.DialogPurpose
import com.nrkei.project.issue.Issue.State.OPEN
import java.util.*

// Purpose: Understands Dialogs that should be invoked in a particular circumstance
class Conversation private constructor(
    private val dialogsByPurpose: MutableMap<DialogPurpose, Dialog>,
    @Suppress("unused") val conversationId: UUID = UUID.randomUUID()
) {
    private val dialogsByIssue = dialogsByPurpose.map { (purpose, dialog) -> purpose.issue to dialog }.toMap().toMutableMap()

    constructor(firstDialog: Pair<DialogPurpose, Dialog>, vararg dialogs: Pair<DialogPurpose, Dialog>) :
            this(listOf(firstDialog, *dialogs).toMap().toMutableMap())

    operator fun get(issue: MissingIssue) =
        dialogsByIssue[issue] ?: throw IllegalStateException("No Dialog defined for issue $issue")

    operator fun get(purpose: DialogPurpose) =
        dialogsByPurpose[purpose] ?: throw IllegalStateException("No Dialog defined for purpose $purpose")

    fun clone() = Conversation(
        dialogsByPurpose.mapValues { (_, dialog) -> dialog.clone() }.toMap().toMutableMap()
    )

    fun openIssues() = dialogsByIssue.keys.filter { issue ->
        issue.state() == OPEN &&
        dialogsByIssue[issue]?.let { it.nextQuestionOrNull() != null } ?: false
    }

    data class ConversationDto(
        val conversationId: UUID,
        val missingIssueReasons: List<String>,
        val rejectionIssueReasons: List<String>,
        val contextDto: ContextDto
    )
}
/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import java.util.*

// Purpose: Understands Dialogs that should be invoked in a particular circumstance
class Conversation private constructor(
    private val dialogs: Map<MissingIssue, Dialog>,
    @Suppress("unused") private val conversationId: UUID = UUID.randomUUID()
) {

    constructor(firstDialog: Pair<MissingIssue, Dialog>, vararg dialogs: Pair<MissingIssue, Dialog>) :
            this(listOf(firstDialog, *dialogs).toMap())

    operator fun get(issue: MissingIssue) = dialogs[issue] ?: throw IllegalStateException("No Dialog defined for issue $issue")

    fun clone() = Conversation(
        dialogs.mapValues { (_, value) -> value.clone() }.toMap()
    )
}
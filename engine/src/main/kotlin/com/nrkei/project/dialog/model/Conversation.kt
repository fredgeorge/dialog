/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.dsl.Dialog
import java.util.*

// Purpose: Understands which Dialog should be invoked in which circumstance
class Conversation private constructor(
    private val dialogs: Map<MissingIssue, Dialog>,
    private val conversationId: UUID = UUID.randomUUID()
) {

    constructor(firstDialog: Pair<MissingIssue, Dialog>, vararg dialogs: Pair<MissingIssue, Dialog>) :
            this(listOf(firstDialog, *dialogs).toMap())

    fun clone() = Conversation(
        dialogs.mapValues { (_, value) -> value.clone() }.toMap()
    )
}
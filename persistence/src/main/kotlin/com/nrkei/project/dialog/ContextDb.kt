/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog

import com.nrkei.project.context.Context
import java.util.*

// Purpose: Understands API for Context data persistence
interface ContextDb {
    fun save(conversationId: UUID, context: Context)
    fun context(conversationId: UUID): Context
}
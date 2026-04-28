/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus.SUCCESS

// Understands next action (or no next action) for an Answer
interface Consequence {
    fun status(): DialogStatus
    fun nextQuestionOrNull(): Question? = null
}

object Acceptable: Consequence {
    override fun status() = SUCCESS
}

object Unacceptable: Consequence {
    override fun status() = PROBLEMS
}
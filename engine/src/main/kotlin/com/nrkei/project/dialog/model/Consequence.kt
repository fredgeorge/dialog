/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.model.DialogStatus2.PROBLEMS
import com.nrkei.project.dialog.model.DialogStatus2.SUCCESS

// Understands next action (or no next action) for an Answer
interface Consequence {
    fun status(): DialogStatus2
    fun nextQuestionOrNull(): Question2? = null
}

object Acceptable: Consequence {
    override fun status() = SUCCESS
}

object Unacceptable: Consequence {
    override fun status() = PROBLEMS
}
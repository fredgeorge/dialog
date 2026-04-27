/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

// Understands SOMETHING_DUMMY
interface Question2 : Consequence {
    fun answer(answer: Answer)
    fun nextQuestionOrNull(): Question2?
}
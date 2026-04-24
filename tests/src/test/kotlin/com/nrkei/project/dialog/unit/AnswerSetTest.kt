/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.model.AnswerSet
import com.nrkei.project.dialog.model.QuestionIdentifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AnswerSetTest {

    @Test
    fun equality() {
        val ab = AnswerSet(mapOf(QuestionIdentifier("a") to "a", QuestionIdentifier("b") to "b"))
        val ba = AnswerSet(mapOf(QuestionIdentifier("b") to "b", QuestionIdentifier("a") to "a"))
        assertEquals(ab, ba)
        assertEquals(ab.hashCode(), ba.hashCode())
    }
}
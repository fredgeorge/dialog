/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.model.AnswerSet
import com.nrkei.project.dialog.model.QuestionId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AnswerSetTest {

    @Test
    fun equality() {
        val ab = AnswerSet(mapOf(QuestionId("a") to "a", QuestionId("b") to "b"))
        val ba = AnswerSet(mapOf(QuestionId("b") to "b", QuestionId("a") to "a"))
        assertEquals(ab, ba)
        assertEquals(ab.hashCode(), ba.hashCode())
    }
}
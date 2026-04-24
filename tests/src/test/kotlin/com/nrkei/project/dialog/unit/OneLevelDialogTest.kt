/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.BooleanQuestion
import com.nrkei.project.dialog.model.Choices
import com.nrkei.project.dialog.model.DialogStatus.Companion.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus.Companion.STARTED
import com.nrkei.project.dialog.model.DialogStatus.DialogConclusion.Companion.FAILED
import com.nrkei.project.dialog.model.DialogStatus.DialogConclusion.Companion.SUCCEEDED
import com.nrkei.project.dialog.model.NoUnansweredQuestionsException
import com.nrkei.project.dialog.model.QuestionIdentifier
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Ensures that a true/false question works
class OneLevelDialogTest {
    private val trueFalseId1 = QuestionIdentifier("trueFalse1")
    private val trueFalseId2 = QuestionIdentifier("trueFalse2")
    private val trueFalse1 = { choices: Choices -> BooleanQuestion(trueFalseId1, choices) }
    private val trueFalse2 = { choices: Choices -> BooleanQuestion(trueFalseId2, choices) }

    @Test
    fun `single question`() {
        dialog {
            first ask trueFalse1 answers {
                on answer true conclude SUCCEEDED
                on answer false conclude FAILED
            }
        }.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())
            assertEquals(trueFalseId1, dialog.question(trueFalseId1).id)
            assertThrows<IllegalArgumentException> { dialog.question(trueFalseId2) }
            assertEquals(trueFalseId1, dialog.nextQuestion().id)
            dialog.nextQuestion().be(true)
            assertEquals(SUCCEEDED, dialog.status())
            dialog.question(trueFalseId1).be(false)
            assertEquals(FAILED, dialog.status())
            assertThrows<NoUnansweredQuestionsException> { dialog.nextQuestion() }
        }
    }

    @Test
    fun `empty dialog`() {
        dialog {}.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())
            assertThrows<IllegalStateException> { dialog.nextQuestion() }
        }
    }

    @Test
    fun `two Boolean questions`() {
        dialog {
            first ask trueFalse1 answers {
                on answer true conclude SUCCEEDED
                on answer false conclude FAILED
            }
            then ask trueFalse2 answers {
                on answer true conclude SUCCEEDED
                on answer false conclude FAILED
            }
        }.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())
            assertEquals(trueFalseId1, dialog.nextQuestion().id)
            dialog.nextQuestion().be(false)
            assertEquals(FAILED, dialog.status()) // One question answered, one more to go
            dialog.question(trueFalseId1).be(true)
            assertEquals(STARTED, dialog.status())
            assertEquals(trueFalseId2, dialog.nextQuestion().id)
            dialog.nextQuestion().be(false)
            assertEquals(FAILED, dialog.status()) // First question SUCCEEDED, but second FAILED
            dialog.question(trueFalseId2).be(true) // Change answer for second question
            assertEquals(SUCCEEDED, dialog.status()) // Both questions SUCCEEDED, so dialog SUCCEEDED
            assertThrows<NoUnansweredQuestionsException> { dialog.nextQuestion() } // No more questions!

        }
    }

    @Test
    fun `first keyword required for first question`() {
        assertThrows<IllegalArgumentException> {
            dialog {
                then ask trueFalse1 answers {
                    on answer true conclude SUCCEEDED
                    on answer false conclude FAILED
                }
            }
        }
    }

    @Test
    fun `first keyword only valid for first question`() {
        assertThrows<IllegalArgumentException> {
            dialog {
                first ask trueFalse1 answers {
                    on answer true conclude SUCCEEDED
                    on answer false conclude FAILED
                }
                first ask trueFalse2 answers {
                    on answer true conclude SUCCEEDED
                    on answer false conclude FAILED
                }
            }
        }
    }

    @Test
    fun `two questions cannot have the same id`() {
        dialog {
            first ask trueFalse1 answers {
                on answer true conclude SUCCEEDED
                on answer false conclude FAILED
            }
            then ask trueFalse1 answers {
                on answer true conclude SUCCEEDED
                on answer false conclude FAILED
            }
        }.also { dialog ->
            assertThrows<IllegalStateException> { dialog.question(QuestionIdentifier("trueFalse1")) }
        }
    }
}

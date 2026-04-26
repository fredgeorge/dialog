/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import org.junit.jupiter.api.Test
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.BooleanQuestion
import com.nrkei.project.dialog.model.Choices
import com.nrkei.project.dialog.model.DialogStatus.DialogConclusion.Companion.FAILED
import com.nrkei.project.dialog.model.DialogStatus.DialogConclusion.Companion.SUCCEEDED
import com.nrkei.project.dialog.model.NoUnansweredQuestionsException
import com.nrkei.project.dialog.model.QuestionId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows

class BooleanQuestionTest {
    private val trueFalse = { choices: Choices -> BooleanQuestion("trueFalse", choices) }

    @Test
    fun `Must have two choices`() {
        assertThrows<IllegalArgumentException> {
            dialog {
                first ask trueFalse answers { }
            }
        }
        assertThrows<IllegalArgumentException> {
            dialog {
                first ask trueFalse answers {
                    on answer true conclude SUCCEEDED
                }
            }
        }
    }

    @Test
    fun `Cannot specify the same answer twice`() {
        assertThrows<IllegalArgumentException> {
            dialog {
                first ask trueFalse answers {
                    on answer true conclude SUCCEEDED
                    on answer false conclude FAILED
                    on answer true conclude SUCCEEDED
                }
            }
        }
    }

    @Test
    fun `Choices must be true or false`() {
        assertThrows<IllegalArgumentException> {
            dialog {
                first ask trueFalse answers {
                    on answer "whoops" conclude SUCCEEDED
                }
            }
        }
    }

    @Test
    fun `Choices cannot both be true or both be false`() {
        assertThrows<IllegalArgumentException> {
            dialog {
                first ask trueFalse answers {
                    on answer true conclude SUCCEEDED
                    on answer true conclude SUCCEEDED
                }
            }
        }
        assertThrows<IllegalArgumentException> {
            dialog {
                first ask trueFalse answers {
                    on answer false conclude FAILED
                    on answer false conclude FAILED
                }
            }
        }
    }

    @Test
    fun `Execute simple dialog with minus operator`() {
        dialog {
            first ask trueFalse answers {
                -true conclude SUCCEEDED
                -false conclude FAILED
            }
        }.also { dialog ->
            dialog.nextQuestion().be(true)
            assertEquals(SUCCEEDED, dialog.status())
            assertThrows<NoUnansweredQuestionsException> { dialog.nextQuestion() }

            dialog.reset()
            dialog.nextQuestion().be(false)
            assertEquals(FAILED, dialog.status())
            dialog.question(QuestionId("trueFalse")).be(true)
            assertEquals(SUCCEEDED, dialog.status())
        }
    }
}
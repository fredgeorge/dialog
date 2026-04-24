/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.dsl.Dialog
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.*
import com.nrkei.project.dialog.model.DialogStatus.Companion.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus.Companion.STARTED
import com.nrkei.project.dialog.model.DialogStatus.DialogConclusion.Companion.FAILED
import com.nrkei.project.dialog.model.DialogStatus.DialogConclusion.Companion.SUCCEEDED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MultiLevelDialogTest {

    private val b_1_id = QuestionIdentifier("b_1")
    private val b_1_true_b_id = QuestionIdentifier("b_1_true_b")
    private val b_1_false_b_id = QuestionIdentifier("b_1_false_b")
    private val b_1_true_b_true_b_id = QuestionIdentifier("b_1_true_b_true_b")
    private val b_1_true_b_false_b_id = QuestionIdentifier("b_1_true_b_false_b")
    private val b_2_id = QuestionIdentifier("b_2")
    private val b_2_true_b_id = QuestionIdentifier("b_2_true_b")
    private val b_2_false_b_id = QuestionIdentifier("b_2_false_b")
    private val b_1 = { choices: Choices -> BooleanQuestion(b_1_id, choices) }
    private val b_1_true_b = { choices: Choices -> BooleanQuestion(b_1_true_b_id, choices) }
    private val b_1_false_b = { choices: Choices -> BooleanQuestion(b_1_false_b_id, choices) }
    private val b_1_true_b_true_b = { choices: Choices -> BooleanQuestion(b_1_true_b_true_b_id, choices) }
    private val b_1_true_b_false_b = { choices: Choices -> BooleanQuestion(b_1_true_b_false_b_id, choices) }
    private val b_2 = { choices: Choices -> BooleanQuestion(b_2_id, choices) }
    private val b_2_true_b = { choices: Choices -> BooleanQuestion(b_2_true_b_id, choices) }
    private val b_2_false_b = { choices: Choices -> BooleanQuestion(b_2_false_b_id, choices) }

    private val mutliLevelBooleanDialog: Dialog
        get() = dialog {
            first ask b_1 answers {
                on answer true ask b_1_true_b answers {
                    on answer true ask b_1_true_b_true_b answers {
                        on answer true conclude SUCCEEDED
                        on answer false conclude FAILED
                    }
                    on answer false ask b_1_true_b_false_b answers {
                        on answer true conclude SUCCEEDED
                        on answer false conclude FAILED
                    }
                }
                on answer false ask b_1_false_b answers {
                    on answer true conclude SUCCEEDED
                    on answer false conclude FAILED
                }
            }
            then ask b_2 answers {
                on answer true ask b_2_true_b answers {
                    on answer true conclude SUCCEEDED
                    on answer false conclude FAILED
                }
                on answer false ask b_2_false_b answers {
                    on answer true conclude SUCCEEDED
                    on answer false conclude FAILED
                }
            }
        }

    @Test
    fun `answering on unbalanced Dialog tree`() {
        mutliLevelBooleanDialog.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(b_1_id, dialog.nextQuestion().id)
            dialog.nextQuestion().be(false)  // Q1 is false
            assertEquals(STARTED, dialog.status())

            assertEquals(b_1_false_b_id, dialog.nextQuestion().id)  // Next question is false leg of Q1
            dialog.nextQuestion().be(false) // Answer false on false leg of Q1
            assertEquals(FAILED, dialog.status())

            assertEquals(b_2_id, dialog.nextQuestion().id) // Skip to second question in dialog

            dialog.question(b_1_id).be(true) // Change the answer to Q1
            assertEquals(STARTED, dialog.status())

            assertEquals(b_1_true_b_id, dialog.nextQuestion().id) // Now on true leg of Q1
            dialog.nextQuestion().be(false) // Answer false on true leg of Q1
            assertEquals(STARTED, dialog.status())

            assertEquals(b_1_true_b_false_b_id, dialog.nextQuestion().id) // Now on true leg of Q1
            dialog.nextQuestion().be(false) // Answer false on true leg of Q1
            assertEquals(FAILED, dialog.status())

            assertEquals(b_2_id, dialog.nextQuestion().id) // Skip to second question in dialog

            dialog.question(b_1_true_b_id).be(true) // Change the answer on the true leg
            assertEquals(STARTED, dialog.status())

            assertEquals(b_1_true_b_true_b_id, dialog.nextQuestion().id) // Next is true leg of true leg of Q1
            dialog.nextQuestion().be(true) // Answer true on true-true leg
            assertEquals(STARTED, dialog.status())

            assertEquals(b_2_id, dialog.nextQuestion().id) // Q1 resolved; next is Q2
            dialog.nextQuestion().be(true)
            assertEquals(STARTED, dialog.status())

            assertEquals(b_2_true_b_id, dialog.nextQuestion().id) // Q1 resolved; next is Q2
            dialog.nextQuestion().be(true)
            assertEquals(SUCCEEDED, dialog.status()) // Both questions

            assertNull(dialog.nextQuestionOrNull())
            assertThrows<NoUnansweredQuestionsException> { dialog.nextQuestion() }

            dialog.question(b_2_true_b_id).be(false)
            assertEquals(FAILED, dialog.status())  // Q1 succeeded, but Q2 failed

            assertNull(dialog.nextQuestionOrNull())
            assertThrows<NoUnansweredQuestionsException> { dialog.nextQuestion() }

            dialog.answerSet().also { answerSet ->
                assertEquals(
                    AnswerSet(
                        mapOf(
                            b_1_id to true,
                            b_1_true_b_id to true,
                            b_1_false_b_id to false,
                            b_1_true_b_true_b_id to true,
                            b_1_true_b_false_b_id to false,
                            b_2_id to true,
                            b_2_true_b_id to false,
                        )
                    ),
                    answerSet
                )

                // Restore Dialog from an AnswerSet
                dialog.reset()
                assertEquals(NOT_STARTED, dialog.status())
                dialog.apply(answerSet)
                assertEquals(FAILED, dialog.status())
            }
        }
    }

    @Test
    fun `Apply Answers to a Dialog tree`() {
        mutliLevelBooleanDialog.also { dialog ->
            dialog.apply(b_1_id to true) // Set one answer
            assertEquals(STARTED, dialog.status())

            dialog.apply() // Reset all answers implicitly
            assertEquals(NOT_STARTED, dialog.status())

            dialog.apply(
                b_1_id to true,
                b_1_true_b_id to true,
                b_1_true_b_true_b_id to true,
                b_2_id to true,
                b_2_true_b_id to true
            )
            assertEquals(SUCCEEDED, dialog.status())

            dialog.question(b_1_id).reset()  // Reset near top of tree
            assertEquals(STARTED, dialog.status())

            dialog.question(b_1_id).be(true)  // Set back to true to complete tree
            assertEquals(SUCCEEDED, dialog.status())
        }
    }
}
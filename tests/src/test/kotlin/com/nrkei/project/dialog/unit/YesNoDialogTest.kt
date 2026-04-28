/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.dsl.dialog2
import com.nrkei.project.dialog.model.*
import com.nrkei.project.dialog.model.DialogStatus2.*
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.YES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Ensures YesNoQuestion works correctly
internal class YesNoDialogTest {
    private val haveSpouse = YesNoQuestion("Have Spouse")
    private val haveCoApplicant = YesNoQuestion("Have Co-applicant")
    private val haveChildren = YesNoQuestion("Have Children")

    @Test fun `Simple valid dialog`() {
        dialog2 {
            first ask haveSpouse answers {
                -YES conclude Unacceptable
                -NO conclude Acceptable
            }
            then ask haveCoApplicant answers {
                -YES conclude Acceptable
                -NO conclude Unacceptable
            }
        }.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(haveSpouse, dialog.nextQuestionOrNull())
            haveSpouse.answer(YES)
            assertEquals(PROBLEMS, dialog.status())
            haveSpouse.answer(NO)
            assertEquals(IN_PROGRESS, dialog.status())

            assertEquals(haveCoApplicant, dialog.nextQuestionOrNull())
            haveCoApplicant.answer(YES)
            assertEquals(SUCCESS, dialog.status())
            haveCoApplicant.answer(NO)
            assertEquals(PROBLEMS, dialog.status())

            assertNull(dialog.nextQuestionOrNull())
        }
    }

    @Test fun `Must handle all possible answers`() {
        assertThrows<IllegalArgumentException> {
            dialog2 {
                first ask haveSpouse answers {
                    -YES conclude Unacceptable // To few answers
                }
            }
        }
        assertThrows<IllegalArgumentException> {
            dialog2 {
                first ask haveSpouse answers {
                    -YES conclude Unacceptable
                    -YES conclude Acceptable    // Duplicate answer
                }
            }
        }
        assertThrows<IllegalArgumentException> {
            dialog2 {
                first ask haveSpouse answers {
                    -YES conclude Unacceptable
                    -NO conclude Acceptable
                    -YES conclude Unacceptable  // Too many answers
                }
            }
        }
    }

    @Test fun `2-level valid dialog`() {
        dialog2 {
            first ask haveSpouse answers {
                -YES ask haveCoApplicant answers {
                    -YES conclude Acceptable
                    -NO conclude Unacceptable
                }
                -NO conclude Acceptable
            }
        }.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(haveSpouse, dialog.nextQuestionOrNull())
            haveSpouse.answer(NO)
            assertEquals(SUCCESS, dialog.status())
            haveSpouse.answer(YES)
            assertEquals(IN_PROGRESS, dialog.status())

            assertEquals(haveCoApplicant, dialog.nextQuestionOrNull())
            haveCoApplicant.answer(YES)
            assertEquals(SUCCESS, dialog.status())
            haveCoApplicant.answer(NO)
            assertEquals(PROBLEMS, dialog.status())

            assertNull(dialog.nextQuestionOrNull())
        }
    }

    @Test fun `3-level valid dialog`() {
        dialog2 {
            first ask haveSpouse answers {
                -YES ask haveCoApplicant answers {
                    -YES conclude Acceptable
                    -NO conclude Unacceptable
                }
                -NO ask haveCoApplicant answers {
                    -YES ask haveChildren answers {
                        -YES conclude Acceptable
                        -NO conclude Acceptable
                    }
                    -NO conclude Unacceptable
                }
            }
        }.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(haveSpouse, dialog.nextQuestionOrNull())
            haveSpouse.answer(NO)
            assertEquals(IN_PROGRESS, dialog.status())

            assertEquals(haveCoApplicant, dialog.nextQuestionOrNull())
            haveCoApplicant.answer(YES)
            assertEquals(IN_PROGRESS, dialog.status())

            assertEquals(haveChildren, dialog.nextQuestionOrNull())
            haveChildren.answer(YES)
            assertEquals(SUCCESS, dialog.status())

            assertNull(dialog.nextQuestionOrNull())
        }
    }
}
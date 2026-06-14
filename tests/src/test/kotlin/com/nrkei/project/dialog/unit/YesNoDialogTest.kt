/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.context.ContextLabelRegistry
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.Acceptable
import com.nrkei.project.dialog.model.DialogStatus.*
import com.nrkei.project.dialog.model.problem
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES
import com.nrkei.project.template.util.TestPurpose
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Ensures YesNoQuestion works correctly
internal class YesNoDialogTest {
    private lateinit var haveSpouse: YesNoQuestion
    private lateinit var haveCoApplicant: YesNoQuestion
    private lateinit var haveSpouseCoApplicant: YesNoQuestion
    private lateinit var haveChildren: YesNoQuestion

    @BeforeEach
    fun setup() {
        ContextLabelRegistry.reset()
        haveSpouse = YesNoQuestion("Have Spouse")
        haveCoApplicant = YesNoQuestion("Have Co-applicant")
        haveSpouseCoApplicant = YesNoQuestion("Have Spouse Co-applicant")
        haveChildren = YesNoQuestion("Have Children")
    }

    @Test
    fun `Simple valid dialog`() {
        (TestPurpose dialog {
            first ask haveSpouse answers {
                -YES conclude problem("Can't have spouse")
                -NO conclude Acceptable
            }
            then ask haveCoApplicant answers {
                -YES conclude Acceptable
                -NO conclude problem("Can't have co-applicant")
            }
        }).also { dialog ->
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

    @Test
    fun `Must handle all possible answers`() {
        assertThrows<IllegalArgumentException> {
            TestPurpose dialog {
                first ask haveSpouse answers {
                    -YES conclude Acceptable // To few answers
                }
            }
        }
        assertThrows<IllegalArgumentException> {
            TestPurpose dialog {
                first ask haveSpouse answers {
                    -YES conclude Acceptable
                    -YES conclude Acceptable    // Duplicate answer
                }
            }
        }
        assertThrows<IllegalArgumentException> {
            TestPurpose dialog {
                first ask haveSpouse answers {
                    -YES conclude Acceptable
                    -NO conclude Acceptable
                    -YES conclude Acceptable  // Too many answers
                }
            }
        }
    }

    @Test
    fun `2-level valid dialog`() {
        (TestPurpose dialog {
            first ask haveSpouse answers {
                -YES ask haveSpouseCoApplicant answers {
                    -YES conclude Acceptable
                    -NO conclude problem("Must have spouse as co-applicant")
                }
                -NO conclude Acceptable
            }
        }).also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(haveSpouse, dialog.nextQuestionOrNull())
            haveSpouse.answer(NO)
            assertEquals(SUCCESS, dialog.status())
            haveSpouse.answer(YES)
            assertEquals(IN_PROGRESS, dialog.status())

            assertEquals(haveSpouseCoApplicant, dialog.nextQuestionOrNull())
            haveSpouseCoApplicant.answer(YES)
            assertEquals(SUCCESS, dialog.status())
            haveSpouseCoApplicant.answer(NO)
            assertEquals(PROBLEMS, dialog.status())

            assertNull(dialog.nextQuestionOrNull())
        }
    }

    @Test
    fun `3-level valid dialog`() {
        (TestPurpose dialog {
            first ask haveSpouse answers {
                -YES ask haveSpouseCoApplicant answers {
                    -YES conclude Acceptable
                    -NO conclude problem("Must have spouse as co-applicant")
                }
                -NO ask haveCoApplicant answers {
                    -YES ask haveChildren answers {
                        -YES conclude Acceptable
                        -NO conclude Acceptable
                    }
                    -NO conclude Acceptable
                }
            }
        }).also { dialog ->
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
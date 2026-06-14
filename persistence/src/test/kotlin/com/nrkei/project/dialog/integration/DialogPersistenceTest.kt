/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.integration

import com.nrkei.project.context.Context
import com.nrkei.project.context.fromJson
import com.nrkei.project.context.toJson
import com.nrkei.project.dialog.model.DialogStatus
import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES
import com.nrkei.project.dialog.visitors.ExtractValues
import com.nrkei.project.dialog.visitors.RestoreValues
import com.nrkei.project.template.util.CoApplicantDialog.CoApplicant
import com.nrkei.project.template.util.SalaryDialog.HAS_INCOME_DOCUMENTATION_BEEN_UPLOADED
import com.nrkei.project.template.util.SalaryDialog.Salary
import com.nrkei.project.template.util.TestConversation.testConversation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// Purpose: Ensures that Dialog Context is persisted correctly
internal class DialogPersistenceTest {

    @Test
    fun `Capture and restore answers for multiple questions`() {
        testConversation.also { conversation ->
            Context().also { originalContext ->
                conversation[CoApplicant.issue].also { firstDialog ->
                    firstDialog.nextQuestionOrNull()?.answer(YES)
                    firstDialog.nextQuestionOrNull()?.answer(YES)
                    firstDialog.nextQuestionOrNull()?.answer("01234567890")
                    assertNull(firstDialog.nextQuestionOrNull())
                    assertEquals(PROBLEMS, firstDialog.status())
                    ExtractValues(firstDialog, originalContext)
                }
                conversation[Salary.issue].also { secondDialog ->
                    secondDialog.nextQuestionOrNull()?.answer(NO)
                    secondDialog.nextQuestionOrNull()?.answer(100_000)
                    assertEquals(
                        HAS_INCOME_DOCUMENTATION_BEEN_UPLOADED,
                        secondDialog.nextQuestionOrNull()?.label
                    )
                    assertEquals(DialogStatus.IN_PROGRESS, secondDialog.status())
                    ExtractValues(secondDialog, originalContext)
                }
                val json = originalContext.toJson()
                Context.fromJson(json).also { restoredContext ->
                    conversation[CoApplicant.issue].also { firstRestoredDialog ->
                        RestoreValues(firstRestoredDialog, restoredContext)
                        ExtractValues(firstRestoredDialog, restoredContext)
                        assertEquals(PROBLEMS, firstRestoredDialog.status())
                        assertNull(firstRestoredDialog.nextQuestionOrNull())
                    }
                    conversation[Salary.issue].also { secondRestoredDialog ->
                        ExtractValues(secondRestoredDialog, restoredContext)
                        assertEquals(
                            HAS_INCOME_DOCUMENTATION_BEEN_UPLOADED,
                            secondRestoredDialog.nextQuestionOrNull()?.label
                        )
                        assertEquals(DialogStatus.IN_PROGRESS, secondRestoredDialog.status())
                    }

                }
            }

        }
    }
}
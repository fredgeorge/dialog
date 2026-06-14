/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.context.ContextLabelRegistry
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.Acceptable
import com.nrkei.project.dialog.model.DialogStatus
import com.nrkei.project.dialog.model.DialogStatus.NOT_STARTED
import com.nrkei.project.dialog.model.DialogStatus.SUCCESS
import com.nrkei.project.dialog.questions.TextQuestion
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.SUFFICIENT
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.TOO_SHORT
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES
import com.nrkei.project.template.util.TestPurpose
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

// Ensures that TextQuestions work
internal class TextDialogTest {
    private lateinit var serviceRequest: TextQuestion
    private lateinit var isApproved: YesNoQuestion

    @BeforeEach
    fun setup() {
        ContextLabelRegistry.reset()
        serviceRequest = TextQuestion("Service Request", minLength = 10)
        isApproved = YesNoQuestion("Is Approved")
    }

    @Test fun `Text with approval`() {
        (TestPurpose dialog {
            first ask serviceRequest answers {
                -TOO_SHORT problem "Service explanation is too short"
                -SUFFICIENT ask isApproved answers {
                    -YES conclude Acceptable
                    -NO problem "Service request rejected"
                }
            }
        }).also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(serviceRequest, dialog.nextQuestionOrNull())
            serviceRequest.answer("lazy")
            assertEquals(DialogStatus.PROBLEMS, dialog.status())

            serviceRequest.answer("This is a very long service request that is sufficient")
            assertEquals(DialogStatus.IN_PROGRESS, dialog.status())

            assertEquals(isApproved, dialog.nextQuestionOrNull())
            isApproved.answer(YES)
            assertEquals(SUCCESS, dialog.status())

            assertNull(dialog.nextQuestionOrNull())
        }
    }

    @Test fun `Invalid minimum length`() {
        assertThrows<IllegalArgumentException> { TextQuestion("Invalid", minLength = 0) }
    }
}
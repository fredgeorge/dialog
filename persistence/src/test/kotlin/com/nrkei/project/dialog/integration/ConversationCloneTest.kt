/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.integration

import com.nrkei.project.dialog.model.DialogStatus.*
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES
import com.nrkei.project.template.util.CoApplicantDialog.coApplicantIssue
import com.nrkei.project.template.util.SalaryDialog.salaryIssue
import com.nrkei.project.template.util.TestConversation.testConversation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

// Purpose: Ensure Conversation generation works correctly
internal class ConversationCloneTest {

    @Test
    fun `Clone yes-no and integer question types`() {
        testConversation.also { original ->
            original.clone()[salaryIssue].also { firstDialogCopy ->
                assertEquals(NOT_STARTED, firstDialogCopy.status())
                firstDialogCopy.nextQuestionOrNull()?.answer(NO)
                firstDialogCopy.nextQuestionOrNull()?.answer(100_000)
                assertEquals(IN_PROGRESS, firstDialogCopy.status())
            }
            original.clone()[salaryIssue].also { secondDialogCopy ->
                assertEquals(NOT_STARTED, secondDialogCopy.status())
                secondDialogCopy.nextQuestionOrNull()?.answer(NO)
                assertEquals(IN_PROGRESS, secondDialogCopy.status())
            }
        }
    }

    @Test
    fun `Clone yes-no and text question types`() {
        testConversation.also { original ->
            original.clone()[coApplicantIssue].also { firstDialogCopy ->
                assertEquals(NOT_STARTED, firstDialogCopy.status())
                firstDialogCopy.nextQuestionOrNull()?.answer(YES)
                firstDialogCopy.nextQuestionOrNull()?.answer(YES)
                firstDialogCopy.nextQuestionOrNull()?.answer("12345678901")
                assertEquals(PROBLEMS, firstDialogCopy.status())
            }
            original.clone()[coApplicantIssue].also { secondDialogCopy ->
                assertEquals(NOT_STARTED, secondDialogCopy.status())
                secondDialogCopy.nextQuestionOrNull()?.answer(NO)
                secondDialogCopy.nextQuestionOrNull()?.answer(YES)
                secondDialogCopy.nextQuestionOrNull()?.answer("98765432109")
                assertEquals(PROBLEMS, secondDialogCopy.status())
            }
        }
    }
}
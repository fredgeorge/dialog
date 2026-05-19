/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.integration

import com.nrkei.project.dialog.model.DialogStatus
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.template.util.SalaryDialog
import com.nrkei.project.template.util.TestConversation
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Purpose: Ensure Conversation works correctly
internal class ConversationTest {

    @Test
    fun `clone all question types`() {
        TestConversation.testConversation.also { original ->
            original.clone()[SalaryDialog.salaryIssue].also { firstDialogCopy ->
                Assertions.assertEquals(DialogStatus.NOT_STARTED, firstDialogCopy.status())
                firstDialogCopy.nextQuestionOrNull()?.answer(YesNoQuestion.YesNoChoice.NO)
                Assertions.assertEquals(DialogStatus.IN_PROGRESS, firstDialogCopy.status())
            }
            original.clone()[SalaryDialog.salaryIssue].also { secondDialogCopy ->
                Assertions.assertEquals(DialogStatus.NOT_STARTED, secondDialogCopy.status())
                secondDialogCopy.nextQuestionOrNull()?.answer(YesNoQuestion.YesNoChoice.NO)
                Assertions.assertEquals(DialogStatus.IN_PROGRESS, secondDialogCopy.status())
            }
        }
    }
}
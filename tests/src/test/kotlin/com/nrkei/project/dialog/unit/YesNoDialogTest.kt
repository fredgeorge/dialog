/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.dsl.dialog2
import com.nrkei.project.dialog.model.Acceptable
import com.nrkei.project.dialog.model.Unacceptable
import com.nrkei.project.dialog.model.YesNoQuestion
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.YES
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

// Understands SOMETHING_DUMMY
internal class YesNoDialogTest {
    private val haveSpouse = YesNoQuestion("Have Spouse")
    private val haveCoApplicant = YesNoQuestion("Have Co-applicant")

    @Test fun `Simple valid dialog`() {
        dialog2 {
            first ask haveSpouse answers {
                -YES conclude Acceptable
                -NO conclude Unacceptable
            }
            then ask haveCoApplicant answers {
                -YES conclude Acceptable
                -NO conclude Unacceptable
            }
        }.also { dialog ->
            assertEquals(haveSpouse, dialog.nextQuestionOrNull())
            haveSpouse.answer(YES)
            assertEquals(haveCoApplicant, dialog.nextQuestionOrNull())
            haveCoApplicant.answer(NO)
            assertNull(dialog.nextQuestionOrNull())
        }
    }
}
/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.integration

import com.nrkei.project.context.Context
import com.nrkei.project.context.enumCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DialogStatus.PROBLEMS
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES
import com.nrkei.project.dialog.visitors.ExtractValues
import com.nrkei.project.template.util.CoApplicantDialog.HAVE_CO_APPLICANT_ID
import com.nrkei.project.template.util.CoApplicantDialog.HAVE_CO_APPLICANT_QUESTION
import com.nrkei.project.template.util.CoApplicantDialog.HAVE_SPOUSE_QUESTION
import com.nrkei.project.template.util.CoApplicantDialog.coApplicantDialog
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test


// Purpose: Ensures that Question values can be extracted from a dialog
internal class CaptureAnswersTest {

    @Test
    fun `Extract yes-no answers`() {
        coApplicantDialog.also { dialog ->
            dialog.nextQuestionOrNull()?.answer(YES) // Have spouse
            dialog.nextQuestionOrNull()?.answer(NO) // Have no co-applicant
            Context().also { context ->
                ExtractValues(dialog, context)
                assertEquals(YES, context[label(HAVE_SPOUSE_QUESTION, enumCodec<YesNoChoice>())])
                assertEquals(NO, context[label(HAVE_CO_APPLICANT_QUESTION, enumCodec<YesNoChoice>())])

                // Change answers
                dialog.question(HAVE_SPOUSE_QUESTION).answer(NO)
                dialog.question(HAVE_CO_APPLICANT_QUESTION).answer(YES)
                ExtractValues(dialog, context)
                assertEquals(NO, context[label(HAVE_SPOUSE_QUESTION, enumCodec<YesNoChoice>())])
                assertEquals(YES, context[label(HAVE_CO_APPLICANT_QUESTION, enumCodec<YesNoChoice>())])
                assertEquals(HAVE_CO_APPLICANT_ID, dialog.nextQuestionOrNull()?.label)

                // Complete questions
                dialog.nextQuestionOrNull()?.answer("12345678901")
                assertNull(dialog.nextQuestionOrNull())
                assertEquals(PROBLEMS, dialog.status())
            }
        }
    }
}
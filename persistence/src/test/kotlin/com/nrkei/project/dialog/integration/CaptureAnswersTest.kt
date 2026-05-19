/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.integration

import com.nrkei.project.context.Context
import com.nrkei.project.context.enumCodec
import com.nrkei.project.context.label
import com.nrkei.project.dialog.model.DialogStatus
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.visitors.ExtractValues
import com.nrkei.project.dialog.visitors.RestoreValues
import com.nrkei.project.template.util.CoApplicantDialog
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

// Purpose: Ensures that Question values can be extracted from a dialog
internal class CaptureAnswersTest {

    @Test
    fun `Extract answers`() {
        CoApplicantDialog.coApplicantDialog.also { dialog ->
            dialog.nextQuestionOrNull()?.answer(YesNoQuestion.YesNoChoice.YES) // Have spouse
            dialog.nextQuestionOrNull()?.answer(YesNoQuestion.YesNoChoice.NO) // Have no co-applicant
            Context().also { context ->
                ExtractValues(dialog, context)
                Assertions.assertEquals(
                    YesNoQuestion.YesNoChoice.YES,
                    context[label(CoApplicantDialog.HAVE_SPOUSE_QUESTION, enumCodec<YesNoQuestion.YesNoChoice>())]
                )
                Assertions.assertEquals(
                    YesNoQuestion.YesNoChoice.NO,
                    context[label(CoApplicantDialog.HAVE_CO_APPLICANT_QUESTION, enumCodec<YesNoQuestion.YesNoChoice>())]
                )

                // Change answers
                dialog.question(CoApplicantDialog.HAVE_SPOUSE_QUESTION).answer(YesNoQuestion.YesNoChoice.NO)
                dialog.question(CoApplicantDialog.HAVE_CO_APPLICANT_QUESTION).answer(YesNoQuestion.YesNoChoice.YES)
                ExtractValues(dialog, context)
                Assertions.assertEquals(
                    YesNoQuestion.YesNoChoice.NO,
                    context[label(CoApplicantDialog.HAVE_SPOUSE_QUESTION, enumCodec<YesNoQuestion.YesNoChoice>())]
                )
                Assertions.assertEquals(
                    YesNoQuestion.YesNoChoice.YES,
                    context[label(CoApplicantDialog.HAVE_CO_APPLICANT_QUESTION, enumCodec<YesNoQuestion.YesNoChoice>())]
                )
                Assertions.assertEquals(CoApplicantDialog.HAVE_CO_APPLICANT_ID, dialog.nextQuestionOrNull()?.label)

                // Complete questions
                dialog.nextQuestionOrNull()?.answer("12345678901")
                Assertions.assertNull(dialog.nextQuestionOrNull())
                Assertions.assertEquals(DialogStatus.PROBLEMS, dialog.status())
            }
        }
    }

    @Test
    fun `Restore answers`() {
        Context().also { context ->
            CoApplicantDialog.coApplicantDialog.clone().also { dialog ->
                dialog.nextQuestionOrNull()?.answer(YesNoQuestion.YesNoChoice.NO) // Have spouse
                dialog.nextQuestionOrNull()?.answer(YesNoQuestion.YesNoChoice.YES) // Have no co-applicant
                ExtractValues(dialog, context)
                Assertions.assertEquals(CoApplicantDialog.HAVE_CO_APPLICANT_ID, dialog.nextQuestionOrNull()?.label)
            }
            CoApplicantDialog.coApplicantDialog.clone().also { dialog ->
                Assertions.assertEquals(CoApplicantDialog.HAVE_SPOUSE_QUESTION, dialog.nextQuestionOrNull()?.label)
                RestoreValues(dialog, context)
                Assertions.assertEquals(CoApplicantDialog.HAVE_CO_APPLICANT_ID, dialog.nextQuestionOrNull()?.label)
            }
        }
    }
}
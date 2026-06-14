/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.template.util

import com.nrkei.project.dialog.dsl.DialogPurpose
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.Acceptable
import com.nrkei.project.dialog.model.missing
import com.nrkei.project.dialog.questions.TextQuestion
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.SUFFICIENT
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.TOO_SHORT
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES

// Purpose: Understand if co-applicant exists/needed for Loan
object CoApplicantDialog {

    object CoApplicant : DialogPurpose {
        private const val CO_APPLICANT = "CoApplicant ID"
        override val name = CO_APPLICANT
        override val issue = missing(CO_APPLICANT)
    }

    const val HAVE_SPOUSE_QUESTION = "Have Spouse?"
    const val HAVE_CO_APPLICANT_QUESTION = "Have Co-applicant?"
    const val HAVE_CO_APPLICANT_ID = "CoApplicant ID"

    private val haveSpouse = YesNoQuestion(HAVE_SPOUSE_QUESTION)
    private val haveCoApplicant = YesNoQuestion(HAVE_CO_APPLICANT_QUESTION)
    private val coApplicantId = TextQuestion(HAVE_CO_APPLICANT_ID, 11)

    val coApplicantDialog = CoApplicant dialog {
        first ask haveSpouse answers {
            -YES ask haveCoApplicant answers {
                -YES ask coApplicantId answers {
                    -SUFFICIENT conclude missing("Need co-applicant approval")
                    -TOO_SHORT problem "National ID should be 11 digits"
                }
                -NO problem "Spouse must be co-applicant"
            }
            -NO ask haveCoApplicant answers {
                -YES ask coApplicantId answers {
                    -SUFFICIENT conclude missing("Need co-applicant approval")
                    -TOO_SHORT problem "National ID should be 11 digits"
                }
                -NO conclude Acceptable
            }
        }
    }
}

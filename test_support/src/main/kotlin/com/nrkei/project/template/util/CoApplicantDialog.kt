/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.template.util

import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.Acceptable
import com.nrkei.project.dialog.model.missing
import com.nrkei.project.dialog.model.problem
import com.nrkei.project.dialog.questions.TextQuestion
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.SUFFICIENT
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.TOO_SHORT
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES

// Purpose: Understand if co-applicant exists/needed for Loan
object CoApplicantDialog {
    private val haveSpouse = YesNoQuestion("Have Spouse?")
    private val haveCoApplicant = YesNoQuestion("Have Co-applicant?")
    private val haveSpouseCoApplicant = YesNoQuestion("Have Spouse Co-applicant?")
    private val spouseId = TextQuestion("Spouse ID", 11)
    private val coApplicantId = TextQuestion("CoApplicant ID", 11)

    val coApplicantDialog = dialog {
        first ask haveSpouse answers {
            -YES ask haveSpouseCoApplicant answers {
                -YES ask spouseId answers {
                    -SUFFICIENT conclude missing("Need co-applicant approval")
                    -TOO_SHORT conclude problem("National ID should be 11 digits")
                }
                -NO conclude problem("Spouse must be co-applicant")
            }
            -NO ask haveCoApplicant answers {
                -YES ask coApplicantId answers {
                    -SUFFICIENT conclude missing("Need co-applicant approval")
                    -TOO_SHORT conclude problem("National ID should be 11 digits")
                }
                -NO conclude Acceptable
            }
        }
    }

    val coApplicantIssue = missing("Do you have a co-applicant?")
}

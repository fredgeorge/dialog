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
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES

// Understands who is applying for a Loan
object CoApplicantDialog {
    val coApplicantDialog = dialog {
        first ask haveSpouse answers {
            -YES ask haveSpouseCoApplicant answers {
                -YES conclude missing("Requires co-applicant approval")
                -NO conclude problem("Spouse must be co-applicant")
            }
            -NO ask haveCoApplicant answers {
                -YES conclude missing("Requires co-applicant approval")
                -NO conclude Acceptable
            }
        }
    }

    private val haveSpouse = YesNoQuestion("Have Spouse?")
    private val haveCoApplicant = YesNoQuestion("Have Co-applicant?")
    private val haveSpouseCoApplicant = YesNoQuestion("Have Spouse Co-applicant?")
}

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
import com.nrkei.project.dialog.questions.IntRangeQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES

// Understands who is applying for a Loan
object DependentsDialog {
    val dependentDialog = dialog {
        first ask haveChildren answers {
            -YES ask howManyChildren answers {
                -IntRangeQuestion.PositiveIntRange.VALID conclude Acceptable
                -IntRangeQuestion.PositiveIntRange.INVALID conclude problem("Invalid other dependent count")
            }
            -NO conclude Acceptable
        }
        then ask haveOtherDependents answers {
            -YES ask howManyDependents answers {
                -IntRangeQuestion.PositiveIntRange.VALID conclude missing("Requires co-applicant approval")
                -IntRangeQuestion.PositiveIntRange.INVALID conclude problem("Invalid children count")
            }
            -NO conclude Acceptable
        }
    }

    private val haveChildren = YesNoQuestion("Have Children?")
    private val howManyChildren = IntRangeQuestion.positiveInt("How Many Children?")
    private val haveOtherDependents = YesNoQuestion("Have Other Dependents?")
    private val howManyDependents = IntRangeQuestion.positiveInt("How Many Other Dependents?")
}

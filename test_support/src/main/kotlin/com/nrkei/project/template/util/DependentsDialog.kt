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
import com.nrkei.project.dialog.questions.IntRangeQuestion
import com.nrkei.project.dialog.questions.IntRangeQuestion.PositiveIntRange.INVALID
import com.nrkei.project.dialog.questions.IntRangeQuestion.PositiveIntRange.VALID
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES

// Purpose: Understands dependent counts for expenses
object DependentsDialog {

    object Dependents : DialogPurpose {
        private const val DO_YOU_HAVE_DEPENDENTS = "Do you have dependents?"
        override val name = DO_YOU_HAVE_DEPENDENTS
        override val issue = missing(DO_YOU_HAVE_DEPENDENTS)
    }
    private val haveChildren = YesNoQuestion("Do You Have Children?")
    private val howManyChildren = IntRangeQuestion.positiveInt("How Many Children?")
    private val haveOtherDependents = YesNoQuestion("Do You Have Other Dependents?")
    private val howManyDependents = IntRangeQuestion.positiveInt("How Many Other Dependents?")

    val dependentDialog = Dependents dialog {
        first ask haveChildren answers {
            -YES ask howManyChildren answers {
                -VALID conclude Acceptable
                -INVALID problem "Invalid children count"
            }
            -NO conclude Acceptable
        }
        then ask haveOtherDependents answers {
            -YES ask howManyDependents answers {
                -VALID conclude Acceptable
                -INVALID problem "Invalid other dependent count"
            }
            -NO conclude Acceptable
        }
    }
}

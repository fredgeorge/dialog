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

// Purpose: Understand the accuracy of salary
object SalaryDialog {

    object Salary : DialogPurpose {
        private const val TAXABLE_INCOME = "Is your taxable income accurate?"
        override val name = TAXABLE_INCOME
        override val issue get() = missing(TAXABLE_INCOME)
    }
    const val HAS_INCOME_DOCUMENTATION_BEEN_UPLOADED = "Has income documentation been uploaded?"

    private val taxIncomeCorrect = YesNoQuestion("Is your tax income correct?")
    private val correctIncome = IntRangeQuestion.positiveInt("What is the actual income?")
    private val documentationUploaded = YesNoQuestion(HAS_INCOME_DOCUMENTATION_BEEN_UPLOADED)
    private val beenReviewed = YesNoQuestion("Has the documentation been reviewed?")

    val salaryDialog = Salary dialog {
        first ask taxIncomeCorrect answers {
            -YES conclude Acceptable
            -NO ask correctIncome answers {
                -INVALID problem "Enter a positive income amount"
                -VALID ask documentationUploaded answers {
                    -YES ask beenReviewed answers {
                        -YES conclude Acceptable
                        -NO conclude missing("Documentation needs review")
                    }
                    -NO conclude missing("Documentation needs to be provided")
                }
            }
        }
    }
}
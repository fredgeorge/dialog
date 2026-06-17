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
import com.nrkei.project.dialog.questions.IntRangeQuestion.Companion.positiveInt
import com.nrkei.project.dialog.questions.IntRangeQuestion.PositiveIntRange.INVALID
import com.nrkei.project.dialog.questions.IntRangeQuestion.PositiveIntRange.VALID
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES

// Purpose: Understands possible alternative income sources
object OtherIncomeDialog {

    object OtherIncome : DialogPurpose {
        private const val HAVE_OTHER_INCOME = "Do you have other income sources?"
        override val name = HAVE_OTHER_INCOME
        override val issue get() = missing(HAVE_OTHER_INCOME)
    }

    private val rentalIncome = YesNoQuestion("Do you have rental income?")
    private val alimony = YesNoQuestion("Are you receiving alimony?")
    private val alimonyDetails = positiveInt("What is your monthly alimony amount?")
    private val documentationUploaded = YesNoQuestion("Have you uploaded documentation for alimony?")
    private val guarantorsPossible = YesNoQuestion("Do you have someone who can serve as guarantor?")

    val otherIncomeDialog = OtherIncome dialog {
        first ask rentalIncome answers {
            -YES conclude missing("Need details on rental income(s)")
            -NO conclude Acceptable
        }
        then ask alimony answers {
            -YES ask alimonyDetails answers {
                -INVALID problem "Enter a valid amount for alimony"
                -VALID ask documentationUploaded answers {
                    -YES conclude missing("Review of alimony documentation needed")
                    -NO conclude missing("Upload documentation for alimony")
                }
            }
            -NO conclude Acceptable
        }
        then ask guarantorsPossible answers {
            -YES conclude missing("Need information about guarantors")
            -NO conclude Acceptable
        }
    }
}
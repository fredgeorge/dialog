/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.template.util

import com.nrkei.project.dialog.dsl.DialogPurpose
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.missing
import com.nrkei.project.dialog.model.problem
import com.nrkei.project.dialog.questions.IntRangeQuestion.Companion.positiveInt
import com.nrkei.project.dialog.questions.IntRangeQuestion.PositiveIntRange.INVALID
import com.nrkei.project.dialog.questions.IntRangeQuestion.PositiveIntRange.VALID
import com.nrkei.project.dialog.questions.TextQuestion
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.SUFFICIENT
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.TOO_SHORT
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES
import com.nrkei.project.template.util.TestConversation.NEED_SALARY_INFORMATION

// Purpose: Understand if co-applicant exists/needed for Loan
object LoginDialog {

    object Login : DialogPurpose {
        private const val WHO_ARE_YOU = "Who are you?"
        override val name = WHO_ARE_YOU
        override val issue = missing(WHO_ARE_YOU)
    }

    const val WHAT_IS_YOUR_ID = "What is your ID?"
    const val SALARY_AUTHORIZATION = "May I pull salary information?"
    const val LOAN_AMOUNT = "How much are you borrowing?"

    private val haveId = TextQuestion(WHAT_IS_YOUR_ID, 11)
    private val salaryAuthorization = YesNoQuestion(SALARY_AUTHORIZATION)
    private val loanAmount = positiveInt(LOAN_AMOUNT)

    val loginDialog = Login dialog {
        first ask haveId answers {
            -SUFFICIENT ask loanAmount answers {
                -VALID ask salaryAuthorization answers {
                    -YES conclude missing(NEED_SALARY_INFORMATION)
                    -NO conclude problem("Cannot process a loan without salary confirmation")
                }
                -INVALID conclude problem("Loan amount is required")
            }
            -TOO_SHORT conclude problem("National ID should be 11 digits")
            }
        }
}

/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.template.util

import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.missing
import com.nrkei.project.dialog.model.problem
import com.nrkei.project.dialog.questions.TextQuestion
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.SUFFICIENT
import com.nrkei.project.dialog.questions.TextQuestion.TextResult.TOO_SHORT
import com.nrkei.project.dialog.questions.YesNoQuestion
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.questions.YesNoQuestion.YesNoChoice.YES
import com.nrkei.project.template.util.TestConversation.NEED_SALARY_INFORMATION

// Purpose: Understand if co-applicant exists/needed for Loan
object LoginDialog {

    const val WHO_ARE_YOU = "Who are you?"
    const val WHAT_IS_YOUR_ID = "What is your ID?"
    const val SALARY_AUTHORIZATION = "May I pull salary information"

    private val haveId = TextQuestion(WHAT_IS_YOUR_ID, 11)
    private val salaryAuthorization = YesNoQuestion(SALARY_AUTHORIZATION)

    val loginDialog = dialog {
        first ask haveId answers {
            -SUFFICIENT ask salaryAuthorization answers {
                -YES conclude missing(NEED_SALARY_INFORMATION)
                -NO conclude problem("Cannot process a loan without salary confirmation")
            }
            -TOO_SHORT conclude problem("National ID should be 11 digits")
            }
        }

    val loginIssue = missing(WHO_ARE_YOU)
}

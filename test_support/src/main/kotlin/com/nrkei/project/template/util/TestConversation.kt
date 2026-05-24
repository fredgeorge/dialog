/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.template.util

import com.nrkei.project.dialog.model.Conversation
import com.nrkei.project.template.util.CoApplicantDialog.coApplicantDialog
import com.nrkei.project.template.util.CoApplicantDialog.coApplicantIssue
import com.nrkei.project.template.util.DependentsDialog.dependentDialog
import com.nrkei.project.template.util.DependentsDialog.dependentIssue
import com.nrkei.project.template.util.LoginDialog.loginDialog
import com.nrkei.project.template.util.LoginDialog.loginIssue
import com.nrkei.project.template.util.OtherIncomeDialog.otherIncomeDialog
import com.nrkei.project.template.util.OtherIncomeDialog.otherIncomeIssue
import com.nrkei.project.template.util.SalaryDialog.salaryDialog
import com.nrkei.project.template.util.SalaryDialog.salaryIssue

// Purpose: Understands all the possible Dialogs with a common purpose
object TestConversation {
    const val NEED_SALARY_INFORMATION = "Need salary information"

    var testConversation = Conversation(
        loginIssue to loginDialog,
        salaryIssue to salaryDialog,
        otherIncomeIssue to otherIncomeDialog,
        coApplicantIssue to coApplicantDialog,
        dependentIssue to dependentDialog
    )
}
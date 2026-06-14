/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.template.util

import com.nrkei.project.dialog.model.Conversation
import com.nrkei.project.template.util.CoApplicantDialog.CoApplicant
import com.nrkei.project.template.util.CoApplicantDialog.coApplicantDialog
import com.nrkei.project.template.util.DependentsDialog.Dependents
import com.nrkei.project.template.util.DependentsDialog.dependentDialog
import com.nrkei.project.template.util.LoginDialog.Login
import com.nrkei.project.template.util.LoginDialog.loginDialog
import com.nrkei.project.template.util.OtherIncomeDialog.OtherIncome
import com.nrkei.project.template.util.OtherIncomeDialog.otherIncomeDialog
import com.nrkei.project.template.util.SalaryDialog.Salary
import com.nrkei.project.template.util.SalaryDialog.salaryDialog

// Purpose: Understands all the possible Dialogs with a common purpose
object TestConversation {
    const val NEED_SALARY_INFORMATION = "Need salary information"

    var testConversation = Conversation(
        Login.issue to loginDialog,
        Salary.issue to salaryDialog,
        OtherIncome.issue to otherIncomeDialog,
        CoApplicant.issue to coApplicantDialog,
        Dependents.issue to dependentDialog
    )
}
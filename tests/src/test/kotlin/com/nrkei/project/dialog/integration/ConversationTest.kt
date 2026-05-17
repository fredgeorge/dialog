/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.integration

import com.nrkei.project.dialog.model.Conversation
import com.nrkei.project.template.util.CoApplicantDialog.coApplicantDialog
import com.nrkei.project.template.util.CoApplicantDialog.coApplicantIssue
import com.nrkei.project.template.util.DependentsDialog.dependentDialog
import com.nrkei.project.template.util.DependentsDialog.dependentIssue
import com.nrkei.project.template.util.OtherIncomeDialog.otherIncomeDialog
import com.nrkei.project.template.util.OtherIncomeDialog.otherIncomeIssue
import com.nrkei.project.template.util.SalaryDialog.salaryDialog
import com.nrkei.project.template.util.SalaryDialog.salaryIssue
import org.junit.jupiter.api.Test

// Purpose: Ensure Conversation works correctly
internal class ConversationTest {

    @Test
    fun `clone all question types`() {
        Conversation(
            salaryIssue to salaryDialog,
            otherIncomeIssue to otherIncomeDialog,
            coApplicantIssue to coApplicantDialog,
            dependentIssue to dependentDialog
        ).also { original ->
            original.clone().also { firstCopy ->

            }
            original.clone().also { secondCopy ->

            }
        }
    }
}
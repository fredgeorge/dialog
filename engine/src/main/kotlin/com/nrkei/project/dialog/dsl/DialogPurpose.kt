package com.nrkei.project.dialog.dsl

import com.nrkei.project.issue.Issue

// Purpose: Understands the reason for a Dialog
interface DialogPurpose {
    val name: String
    val issue: Issue<*>
}
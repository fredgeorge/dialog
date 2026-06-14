package com.nrkei.project.template.util

import com.nrkei.project.dialog.dsl.DialogPurpose
import com.nrkei.project.dialog.model.missing

// Purpose: Provides sample DialogPurpose for testing
object TestPurpose: DialogPurpose {
    private const val NAME = "TestDialogPurpose"
    override val name = NAME
    override val issue = missing(NAME)
}
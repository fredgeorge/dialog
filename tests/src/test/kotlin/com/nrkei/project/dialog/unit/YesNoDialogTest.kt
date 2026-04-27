/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.dialog.dsl.dialog2
import com.nrkei.project.dialog.model.Acceptable
import com.nrkei.project.dialog.model.Unacceptable
import com.nrkei.project.dialog.model.YesNoQuestion
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.NO
import com.nrkei.project.dialog.model.YesNoQuestion.YesNoChoice.YES
import org.junit.jupiter.api.Test

// Understands SOMETHING_DUMMY
internal class YesNoDialogTest {
    private val HAVE_SPOUSE = YesNoQuestion("Have Spouse")

    @Test fun `Simple valid dialog`() {
        dialog2 {
            first ask HAVE_SPOUSE answers {
                -YES conclude Acceptable
                -NO conclude Unacceptable
            }
        }.also { dialog ->
            dialog.nextQuestionOrNull()
        }
    }
}
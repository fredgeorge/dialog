/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.dsl.Dialog

class NoUnansweredQuestionsException(dialog: Dialog)
    : IllegalStateException("No unanswered questions found. Final status is ${dialog.status()}") {

        val status = dialog.status()
    }

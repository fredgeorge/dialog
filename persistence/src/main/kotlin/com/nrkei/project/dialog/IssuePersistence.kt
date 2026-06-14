/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog

import com.nrkei.project.dialog.model.MissingIssue
import com.nrkei.project.dialog.model.MissingIssue.MissingIssueDto

// Understands creation of issues from DTOs
object IssuePersistence {
//    internal fun ProblemIssue.ProblemIssueDto.toIssue() = ProblemIssue(this.description)
    internal fun MissingIssueDto.toIssue() = MissingIssue(this.description)
}
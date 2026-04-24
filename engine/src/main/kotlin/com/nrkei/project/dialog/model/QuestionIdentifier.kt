/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

data class QuestionIdentifier(private val label: String) : Comparable<QuestionIdentifier> {

    override fun compareTo(other: QuestionIdentifier) = label.compareTo(other.label)

    override fun toString() = "<< $label >>"
}
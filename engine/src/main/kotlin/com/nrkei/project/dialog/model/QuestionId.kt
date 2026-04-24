/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

data class QuestionId(private val label: String) : Comparable<QuestionId> {

    override fun compareTo(other: QuestionId) = label.compareTo(other.label)

    override fun toString() = "<< $label >>"
}
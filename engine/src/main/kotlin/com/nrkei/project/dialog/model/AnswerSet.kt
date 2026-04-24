/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

import com.nrkei.project.dialog.dsl.Dialog

// Understands a set of responses to Questions in a Dialog
class AnswerSet(private val answers: Map<QuestionIdentifier, Any>) {

    internal fun applyTo(dialog: Dialog) {
        dialog.reset()
        answers.forEach { (key, value) -> dialog.question(key).be(value) }
    }

    override fun equals(other: Any?) =
        this === other || other is AnswerSet && this.answers == other.answers

    override fun hashCode() = answers.hashCode()

    override fun toString() = "\nAnswerSet:\n\t${answersString()}\n"

    private fun answersString() = answers.toSortedMap().map { (key, value) -> "$key -> $value" }.joinToString("\n\t")
}
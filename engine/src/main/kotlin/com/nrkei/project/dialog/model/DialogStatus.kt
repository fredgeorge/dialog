/*
 * Copyright (c) 2025 by Fred George
 * @author: Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

open class DialogStatus private constructor(idLabel: String) : Question {
    override val id = QuestionIdentifier("<$idLabel>")

    companion object {
        val NOT_STARTED = DialogStatus("NOT_STARTED")
        val STARTED = DialogStatus("STARTED")
    }

    override fun questionOrNull(id: QuestionIdentifier) =
        if (this.id == id) this else null

    override fun nextQuestion() = this

    override fun be(value: Any) = throw IllegalStateException("This is not a valid Question; it cannot be answered")

    override fun reset() {} // do nothing

    override fun resetAll() {} // do nothing

    override fun accept(visitor: QuestionVisitor) {
        throw IllegalStateException("This is not a valid Question; it cannot be in a Dialog")
    }

    override fun status() = this

    override fun toString() = "Status: $id"

    class DialogConclusion private constructor(idLabel: String) : DialogStatus(idLabel) {

        companion object {
            val SUCCEEDED = DialogConclusion("SUCCEEDED")
            val FAILED = DialogConclusion("FAILED")
        }

        override fun be(value: Any) =
            throw IllegalArgumentException("Terminal condition reached; it cannot be answered")

        override fun accept(visitor: QuestionVisitor) {
            return visitor.visit(this)
        }
    }
}

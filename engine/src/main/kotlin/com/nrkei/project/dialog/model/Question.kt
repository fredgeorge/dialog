/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

// PurposeUnderstands solicitation of something specific
interface Question {
    val possibleResults: List<Result>

    fun answer(answer: Any)

    fun result(): Result?

    fun clone(): Question

    fun accept(visitor: DialogVisitor)
}

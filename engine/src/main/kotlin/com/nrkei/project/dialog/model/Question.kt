/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

// Understands solicitation of information
interface Question : Consequence {
    fun answer(answer: Answer)
    fun validateConsequences() {}

    val possibleAnswers: List<Answer>
    val consequences: MutableMap<Answer, Consequence>
}
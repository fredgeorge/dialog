/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

// Manages consequences for a Question, ensuring all possible answers are covered
// and each answer is set only once. The internal map does not store null values.
class QuestionConsequences internal constructor(
    private val allowedResults: List<Result>
) {
    private val consequences = mutableMapOf<Result, Consequence>()

    internal operator fun set(result: Result, consequence: Consequence) {
        require(result in allowedResults) {
            "Answer $result is not a valid choice for this question. Allowed: ${allowedResults.map { it.name }}"
        }
        require(!consequences.containsKey(result)) {
            "Consequence for answer $result has already been set."
        }
        consequences[result] = consequence
    }

    internal operator fun get(result: Result): Consequence = consequences[result]
        ?: throw IllegalStateException("Consequence for answer $result not found.")

    /**
     * Checks if all allowed answers have been assigned a consequence.
     */
    internal fun validate() =
        require(allowedResults.size == consequences.size)
        { "All possible Answers must be specified" }
}
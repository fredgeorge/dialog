/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.model

// Manages consequences for a Question, ensuring all possible answers are covered
// and each answer is set only once. The internal map does not store null values.
class QuestionConsequences internal constructor(
    private val allowedAnswers: List<Answer>
) {
    private val consequences = mutableMapOf<Answer, Consequence>()

    internal operator fun set(answer: Answer, consequence: Consequence) {
        require(answer in allowedAnswers) {
            "Answer $answer is not a valid choice for this question. Allowed: ${allowedAnswers.map { it.name }}"
        }
        require(!consequences.containsKey(answer)) {
            "Consequence for answer $answer has already been set."
        }
        consequences[answer] = consequence
    }

    internal operator fun get(answer: Answer): Consequence = consequences[answer]
        ?: throw IllegalStateException("Consequence for answer $answer not found.")

    /**
     * Checks if all allowed answers have been assigned a consequence.
     */
    internal fun validate() =
        require(allowedAnswers.size == consequences.size)
        { "All possible Answers must be specified" }
}
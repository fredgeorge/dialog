/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.context.ContextLabelRegistry
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.Acceptable
import com.nrkei.project.dialog.model.DialogStatus.*
import com.nrkei.project.dialog.questions.IntRangeQuestion
import com.nrkei.project.dialog.questions.IntRangeQuestion.*
import com.nrkei.project.dialog.questions.IntRangeQuestion.Companion.positiveInt
import com.nrkei.project.dialog.questions.IntRangeQuestion.Companion.zeroOrMoreInt
import com.nrkei.project.dialog.unit.IntRangeDialogTest.AgeRange.*
import com.nrkei.project.dialog.unit.IntRangeDialogTest.NetWorthRange.*
import com.nrkei.project.template.util.TestPurpose
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Ensures that IntQuestions work as expected
internal class IntRangeDialogTest {
    private lateinit var age: IntRangeQuestion<AgeRange>
    private lateinit var netWorth: IntRangeQuestion<NetWorthRange>
    private lateinit var childCount: IntRangeQuestion<NonNegativeIntRange>
    private lateinit var applicantCount: IntRangeQuestion<PositiveIntRange>

    @BeforeEach
    fun setup() {
        ContextLabelRegistry.reset()
        age = IntRangeQuestion("Age", AgeRange::class)
        netWorth = IntRangeQuestion("Net Worth", NetWorthRange::class)
        childCount = zeroOrMoreInt("Child Count")
        applicantCount = positiveInt("Applicant Count")
    }

    @Test fun `Simple valid dialog for age`() {
        (TestPurpose dialog {
            first ask age answers {
                -INVALID problem "Invalid number for age"
                -UNDER_18 problem "Age must be at least 18"
                -ADULT conclude Acceptable
                -SENIOR problem "Not available for senior citizens"
            }
        }).also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(age, dialog.nextQuestionOrNull())
            age.answer(17)
            assertEquals(PROBLEMS, dialog.status())

            age.answer(25)
            assertEquals(SUCCESS, dialog.status())

            age.answer(74)
            assertEquals(PROBLEMS, dialog.status())

            age.answer(-2)
            assertEquals(PROBLEMS, dialog.status())
        }
    }

    @Test fun `Simple valid dialog for net worth`() {
        (TestPurpose dialog {
            first ask netWorth answers {
                -POOR problem "Not enough money"
                -ACCEPTABLE conclude Acceptable
                -WEALTHY conclude Acceptable
            }
        }).also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(netWorth, dialog.nextQuestionOrNull())
            netWorth.answer(-500_000)
            assertEquals(PROBLEMS, dialog.status())

            netWorth.answer(666_666)
            assertEquals(SUCCESS, dialog.status())

            netWorth.answer(7_400_000)
            assertEquals(SUCCESS, dialog.status())
        }
    }

    @Test fun `Convenience Int constructors`() {
        (TestPurpose dialog {
            first ask applicantCount answers {
                -PositiveIntRange.INVALID problem "Must have at least one applicant"
                -PositiveIntRange.VALID conclude Acceptable
            }
            then ask childCount answers {
                -NonNegativeIntRange.INVALID problem "Specify zero or more children"
                -NonNegativeIntRange.VALID conclude Acceptable
            }
        }).also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(applicantCount, dialog.nextQuestionOrNull())
            applicantCount.answer(1)
            assertEquals(childCount, dialog.nextQuestionOrNull())
            childCount.answer(0)
            assertNull(dialog.nextQuestionOrNull())
            assertEquals(SUCCESS, dialog.status())

            applicantCount.answer(0)
            assertEquals(PROBLEMS, dialog.status())

            applicantCount.answer(1)
            childCount.answer(-1)
            assertEquals(PROBLEMS, dialog.status())
        }
    }

    enum class AgeRange(
        override val minimum: Int,
        override val maximum: Int,
    ) : IntRangeResult {
        INVALID(Int.MIN_VALUE, -1),
        UNDER_18(0, 17),
        ADULT(18, 64),
        SENIOR(65, Int.MAX_VALUE)
    }

    enum class NetWorthRange(
        override val minimum: Int,
        override val maximum: Int,
    ) : IntRangeResult {
        POOR(Int.MIN_VALUE, 99_999),
        ACCEPTABLE(100_000, 999_999),
        WEALTHY(1_000_000, Int.MAX_VALUE)
    }
}
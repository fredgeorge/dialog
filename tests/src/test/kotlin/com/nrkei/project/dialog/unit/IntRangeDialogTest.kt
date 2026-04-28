/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog.unit

import com.nrkei.project.context.ContextLabelRegistry
import com.nrkei.project.dialog.dsl.dialog
import com.nrkei.project.dialog.model.*
import com.nrkei.project.dialog.model.DialogStatus.*
import com.nrkei.project.dialog.model.IntRangeQuestion.IntRangeAnswer
import com.nrkei.project.dialog.unit.IntRangeDialogTest.AgeRange.ADULT
import com.nrkei.project.dialog.unit.IntRangeDialogTest.AgeRange.INVALID
import com.nrkei.project.dialog.unit.IntRangeDialogTest.AgeRange.SENIOR
import com.nrkei.project.dialog.unit.IntRangeDialogTest.AgeRange.UNDER_18
import com.nrkei.project.dialog.unit.IntRangeDialogTest.NetWorthRange.ACCEPTABLE
import com.nrkei.project.dialog.unit.IntRangeDialogTest.NetWorthRange.POOR
import com.nrkei.project.dialog.unit.IntRangeDialogTest.NetWorthRange.WEALTHY
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Ensures that IntQuestions work as expected
internal class IntRangeDialogTest {
    private lateinit var age: IntRangeQuestion<AgeRange>
    private lateinit var netWorth: IntRangeQuestion<NetWorthRange>

    @BeforeEach
    fun setup() {
        ContextLabelRegistry.reset()
        age = IntRangeQuestion("Age", AgeRange::class)
        netWorth = IntRangeQuestion("Net Worth", NetWorthRange::class)
    }

    @Test fun `Simple valid dialog for age`() {
        dialog {
            first ask age answers {
                -INVALID conclude Unacceptable
                -UNDER_18 conclude Unacceptable
                -ADULT conclude Acceptable
                -SENIOR conclude Unacceptable
            }
        }.also { dialog ->
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
        dialog {
            first ask netWorth answers {
                -POOR conclude Unacceptable
                -ACCEPTABLE conclude Acceptable
                -WEALTHY conclude Acceptable
            }
        }.also { dialog ->
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

    enum class AgeRange(
        override val minimum: Int,
        override val maximum: Int,
    ) : IntRangeAnswer {
        INVALID(Int.MIN_VALUE, -1),
        UNDER_18(0, 17),
        ADULT(18, 64),
        SENIOR(65, Int.MAX_VALUE)
    }

    enum class NetWorthRange(
        override val minimum: Int,
        override val maximum: Int,
    ) : IntRangeAnswer {
        POOR(Int.MIN_VALUE, 99_999),
        ACCEPTABLE(100_000, 999_999),
        WEALTHY(1_000_000, Int.MAX_VALUE)
    }
}
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
import com.nrkei.project.dialog.model.DoubleRangeQuestion.DoubleRangeAnswer
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.BmiRange.MORBIDLY_OBESE
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.BmiRange.NORMAL
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.BmiRange.OBESE
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.BmiRange.OVERWEIGHT
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.BmiRange.UNDERWEIGHT
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.TemperatureRange.COLD
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.TemperatureRange.HOT
import com.nrkei.project.dialog.unit.DoubleRangeDialogTest.TemperatureRange.WARM
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

// Ensures that DoubleRangeQuestions work as expected
internal class DoubleRangeDialogTest {
    private lateinit var temperature: DoubleRangeQuestion<TemperatureRange>
    private lateinit var bmi: DoubleRangeQuestion<BmiRange>

    @BeforeEach
    fun setup() {
        ContextLabelRegistry.reset()
        temperature = DoubleRangeQuestion("Temperature", TemperatureRange::class)
        bmi = DoubleRangeQuestion("BMI", BmiRange::class)
    }

    @Test fun `Simple valid dialog for temperature in Celsius`() {
        dialog {
            first ask temperature answers {
                -COLD conclude Unacceptable
                -WARM conclude Acceptable
                -HOT conclude Unacceptable
            }
        }.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(temperature, dialog.nextQuestionOrNull())
            temperature.answer(-15.0)               // well below freezing
            assertEquals(PROBLEMS, dialog.status())

            temperature.answer(20.0)                // pleasant
            assertEquals(SUCCESS, dialog.status())

            temperature.answer(35.0)                // sweltering
            assertEquals(PROBLEMS, dialog.status())

            temperature.answer(-273.15)             // absolute zero
            assertEquals(PROBLEMS, dialog.status())
        }
    }

    @Test fun `Temperature accepts Int answer without explicit cast`() {
        dialog {
            first ask temperature answers {
                -COLD conclude Unacceptable
                -WARM conclude Acceptable
                -HOT conclude Unacceptable
            }
        }.also { dialog ->
            assertEquals(temperature, dialog.nextQuestionOrNull())
            temperature.answer(22)                  // Int, not Double
            assertEquals(SUCCESS, dialog.status())
        }
    }

    @Test fun `Simple valid dialog for Body Mass Index`() {
        dialog {
            first ask bmi answers {
                -UNDERWEIGHT    conclude Unacceptable
                -NORMAL         conclude Acceptable
                -OVERWEIGHT     conclude Unacceptable
                -OBESE          conclude Unacceptable
                -MORBIDLY_OBESE conclude Unacceptable
            }
        }.also { dialog ->
            assertEquals(NOT_STARTED, dialog.status())

            assertEquals(bmi, dialog.nextQuestionOrNull())
            bmi.answer(15.0)                        // underweight
            assertEquals(PROBLEMS, dialog.status())

            bmi.answer(22.5)                        // normal
            assertEquals(SUCCESS, dialog.status())

            bmi.answer(27.0)                        // overweight
            assertEquals(PROBLEMS, dialog.status())

            bmi.answer(35.0)                        // obese
            assertEquals(PROBLEMS, dialog.status())

            bmi.answer(45.0)                        // morbidly obese
            assertEquals(PROBLEMS, dialog.status())
        }
    }

    // Celsius temperature ranges; supports negative values
    enum class TemperatureRange(
        override val minimum: Double,
        override val maximum: Double,
    ) : DoubleRangeAnswer {
        COLD(-Double.MAX_VALUE, 14.9),
        WARM(15.0, 27.9),
        HOT(28.0, Double.MAX_VALUE)
    }

    // WHO Body Mass Index tiers (kg/m²)
    enum class BmiRange(
        override val minimum: Double,
        override val maximum: Double,
    ) : DoubleRangeAnswer {
        UNDERWEIGHT(-Double.MAX_VALUE, 18.4),
        NORMAL(18.5, 24.9),
        OVERWEIGHT(25.0, 29.9),
        OBESE(30.0, 39.9),
        MORBIDLY_OBESE(40.0, Double.MAX_VALUE)
    }
}

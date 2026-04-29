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
import com.nrkei.project.dialog.questions.DoubleRangeQuestion
import com.nrkei.project.dialog.questions.DoubleRangeQuestion.DoubleRangeAnswer
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

    @Test fun `Temperature boundary values are unambiguous`() {
        dialog {
            first ask temperature answers {
                -COLD conclude Unacceptable
                -WARM conclude Acceptable
                -HOT conclude Unacceptable
            }
        }.also { dialog ->
            assertEquals(temperature, dialog.nextQuestionOrNull())
            temperature.answer(15.0)                // exactly at COLD/WARM boundary → WARM
            assertEquals(SUCCESS, dialog.status())

            temperature.answer(28.0)                // exactly at WARM/HOT boundary → HOT
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

    @Test fun `BMI boundary values are unambiguous`() {
        dialog {
            first ask bmi answers {
                -UNDERWEIGHT    conclude Unacceptable
                -NORMAL         conclude Acceptable
                -OVERWEIGHT     conclude Unacceptable
                -OBESE          conclude Unacceptable
                -MORBIDLY_OBESE conclude Unacceptable
            }
        }.also { dialog ->
            assertEquals(bmi, dialog.nextQuestionOrNull())
            bmi.answer(18.5)                        // exactly at UNDERWEIGHT/NORMAL boundary → NORMAL
            assertEquals(SUCCESS, dialog.status())

            bmi.answer(25.0)                        // exactly at NORMAL/OVERWEIGHT boundary → OVERWEIGHT
            assertEquals(PROBLEMS, dialog.status())

            bmi.answer(30.0)                        // exactly at OVERWEIGHT/OBESE boundary → OBESE
            assertEquals(PROBLEMS, dialog.status())

            bmi.answer(40.0)                        // exactly at OBESE/MORBIDLY_OBESE boundary → MORBIDLY_OBESE
            assertEquals(PROBLEMS, dialog.status())
        }
    }

    // Celsius temperature ranges; supports negative values
    // Ranges are half-open [minimum, maximum) — boundary values belong to the higher tier
    enum class TemperatureRange(
        override val minimum: Double,
        override val maximum: Double,
    ) : DoubleRangeAnswer {
        COLD(-Double.MAX_VALUE, 15.0),
        WARM(15.0, 28.0),
        HOT(28.0, Double.MAX_VALUE)
    }

    // WHO Body Mass Index tiers (kg/m²)
    // Ranges are half-open [minimum, maximum) — boundary values belong to the higher tier
    enum class BmiRange(
        override val minimum: Double,
        override val maximum: Double,
    ) : DoubleRangeAnswer {
        UNDERWEIGHT(-Double.MAX_VALUE, 18.5),
        NORMAL(18.5, 25.0),
        OVERWEIGHT(25.0, 30.0),
        OBESE(30.0, 40.0),
        MORBIDLY_OBESE(40.0, Double.MAX_VALUE)
    }
}

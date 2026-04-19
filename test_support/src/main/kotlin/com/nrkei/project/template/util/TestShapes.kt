/*
 *  Copyright (c) 2000-2026 by Fred George
 *  May be used freely except for training; license required for training.
 *  @author Fred George  fredgeorge@acm.org
 */

package com.nrkei.project.dialog.util

import com.nrkei.project.dialog.Rectangle

// Provides basic reference shapes for testing
object TestShapes {
    val UNIT_SQUARE = Rectangle(1.0, 1.0)
    val GOLDEN_RECTANGLE = Rectangle(1.618, 1.0)
    val LETTER_PAGE = Rectangle(11.0, 8.5)
}

/*
 * Copyright (c) 2025-26 by Fred George
 * @author Fred George  fredgeorge@acm.org
 * Licensed under the MIT License; see LICENSE file in root.
 */

package com.nrkei.project.dialog

import com.nrkei.project.dialog.Encoding.fromBase64
import com.nrkei.project.dialog.Encoding.toBase64
import com.nrkei.project.dialog.Rectangle.RectangleDto

// Understands persistence of a Rectangle by converting
// it to a DTO, serializing that DTO as JSON, and
// optionally wrapping the JSON in Base64 for
// text-safe storage or transmission.

internal fun Rectangle.toMemento() = toBase64(toDto())

internal fun Rectangle.Companion.fromMemento(memento: String) =
    fromBase64<RectangleDto>(memento).let { rectangleDto ->
        Rectangle(rectangleDto.length, rectangleDto.width)
    }
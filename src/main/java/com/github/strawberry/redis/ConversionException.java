/**
 * Strawberry Library
 * Copyright (C) 2011 - 2012
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package com.github.strawberry.redis;

/**
 *
 * @author Wiehann Matthysen
 */
public final class ConversionException extends RuntimeException {

    ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    ConversionException(String message) {
        super(message);
    }

    static ConversionException of(Exception cause, String toConvert, String key, Class<?> type) {
        return new ConversionException(String.format("Cannot convert value: (%s) at key: (%s) to %s.", toConvert, key, type), cause);
    }

    static ConversionException of(String toConvert, String key, Class<?> type) {
        return new ConversionException(String.format("Cannot convert value: (%s) at key: (%s) to %s.", toConvert, key, type));
    }
}

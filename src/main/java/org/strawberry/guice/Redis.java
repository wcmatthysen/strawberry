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
package org.strawberry.guice;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotate fields of your class into which an
 * {@link com.google.inject.Injector} should inject values from a Redis
 * database.
 * 
 * @author Wiehann Matthysen
 */
@BindingAnnotation
@Target({FIELD, PARAMETER, METHOD})
@Retention(RUNTIME)
@Documented
public @interface Redis {

    /**
     * The key-pattern to use when querying the Redis database for values to be
     * injected into this field.
     */
    String value();

    /**
     * If true and given the key-pattern: the matching Redis key-value pair(s)
     * will be accumulated and injected as a Map into the annotated field.
     * Otherwise, if false, only the matching value(s) will be injected.
     */
    boolean includeKeys() default false;

    /**
     * If true and given the key-pattern, the matching value(s) from the Redis
     * database will be nested inside a collection before being injected into
     * the annotated field.
     * Otherwise, if false, values will be injected as-is, and will only be
     * converted if and when it makes sense.
     */
    boolean alwaysNest() default false;

    /**
     * If false, a default value will be injected for the specified type if no
     * matching value for the specified key-pattern exists in the Redis
     * database. This default value is type dependent and only the following
     * type are supported: {@code char([])} and {@code Character([])},
     * {@code String}, {@code byte([])} and {@code Byte([]}, {@code boolean} and
     * {@code Boolean}, {@code short} and {@code Short}, {@code int} and
     * {@code Integer}, {@code long} and {@cod Long}, {@code BigInteger},
     * {@code float} and {@code Float}, {@code double} and {@code Double},
     * {@code BigDecimal}, {@code Map}, {@code List} and {@code Set}.
     * Otherwise, if true, null will be used as the candidate-value if no
     * matching value for the specified key-pattern exists in the Redis
     * database. However, if {@link Redis#forceUpdate()} is false, then a
     * non-null default field-value will take precedence over a null candidate.
     */
    boolean allowNull() default true;

    /**
     * If true, the retrieved value from the Redis database will always take
     * precedence (even if it is null) over the default field value.
     * Otherwise, default field values will take precedence over a null
     * candidate value (see {@link Redis#allowNull()}).
     */
    boolean forceUpdate() default false;
}

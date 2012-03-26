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
package com.github.strawberry.guice;

import java.lang.reflect.Field;

import com.google.common.cache.LoadingCache;
import com.google.inject.MembersInjector;

import fj.data.Option;

/**
 *
 * @author Wiehann Matthysen
 */
final class RedisMembersInjector<T> implements MembersInjector<T> {

    private final LoadingCache<Field, Option> cache;
    private final Field field;

    RedisMembersInjector(LoadingCache<Field, Option> cache, Field field) {
        this.cache = cache;
        this.field = field;
        this.field.setAccessible(true);
    }

    @Override
    public void injectMembers(final T object) {
        Option value = this.cache.getUnchecked(this.field);
        try {
            Redis annotation = this.field.getAnnotation(Redis.class);
            if (this.field.get(object) != null) {
                // If field is not equal to null (i.e. default value has been set)
                // and if value to be injected is not null, then set.
                // Or, if forced update has been specified, then set.
                if (annotation.forceUpdate() || value.isSome()) {
                    this.field.set(object, value.toNull());
                }
            } else {
                // Always set null field.
                this.field.set(object, value.toNull());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

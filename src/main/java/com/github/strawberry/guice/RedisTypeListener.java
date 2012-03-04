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
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;

import fj.data.Option;

/**
 *
 * @author Wiehann Matthysen
 */
final class RedisTypeListener implements TypeListener {

    private final LoadingCache<Field, Option> cache;

    RedisTypeListener(LoadingCache<Field, Option> cache) {
        this.cache = cache;
    }

    @Override
    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        for (Field field : typeLiteral.getRawType().getDeclaredFields()) {
            if (field.isAnnotationPresent(Redis.class)) {
                typeEncounter.register(new RedisMembersInjector<T>(this.cache, field));
            }
        }
    }
}

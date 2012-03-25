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
package com.github.strawberry.util;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.regex.Pattern;

import fj.F;
import fj.data.Array;
import fj.data.Option;

/**
 *
 * @author Wiehann Matthysen
 */
public final class Types {
    
    public static final Pattern BOOLEAN = Pattern.compile("^t|true|y|yes|1|f|false|n|no|0$", Pattern.CASE_INSENSITIVE);
    
    public static final Pattern TRUE = Pattern.compile("^t|true|y|yes|1$", Pattern.CASE_INSENSITIVE);

    private Types() { }
    
    public static Array<Type> genericTypesOf(Field field) {
        Array<Type> genericTypes = Array.empty();
        Type type = field.getGenericType();
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            genericTypes = Array.array(pType.getActualTypeArguments());
        }
        return genericTypes;
    }
    
    public static Option<Type> genericTypeOf(Field field, int index) {
        Option<Type> genericType = Option.none();
        Type type = field.getGenericType();
        if (field.getGenericType() instanceof ParameterizedType) {
            ParameterizedType pType = (ParameterizedType) type;
            Type[] actualTypeArgs = pType.getActualTypeArguments();
            if (index >= 0 && index < actualTypeArgs.length) {
                genericType = Option.some(pType.getActualTypeArguments()[index]);
            }
        }
        return genericType;
    }
    
    public static F<Type, Boolean> equals(final Class<?> clazz) {
        return new F<Type, Boolean>(){
            @Override
            public Boolean f(Type type) {
                boolean isEqual = false;
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    isEqual = clazz.equals((Class<?>)pType.getRawType());
                } else {
                    isEqual = clazz.equals((Class<?>)type);
                }
                return isEqual;
            }
        };
    }
    
    public static F<Type, Boolean> subTypeOf(final Class<?> clazz) {
        return new F<Type, Boolean>(){
            @Override
            public Boolean f(Type type) {
                boolean isSubType = false;
                if (type instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) type;
                    isSubType = clazz.isAssignableFrom((Class<?>)pType.getRawType());
                } else {
                    isSubType = clazz.isAssignableFrom((Class<?>)type);
                }
                return isSubType;
            }
        };
    }
}

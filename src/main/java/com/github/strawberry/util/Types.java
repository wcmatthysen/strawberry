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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import fj.F;
import fj.data.Array;
import fj.data.Option;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.regex.Pattern;

/**
 *
 * @author Wiehann Matthysen
 */
public final class Types {
    
    public static final Pattern BOOLEAN = Pattern.compile("^(?:t|true|y|yes|1|f|false|n|no|0)$", Pattern.CASE_INSENSITIVE);
    
    public static final Pattern TRUE = Pattern.compile("^(?:t|true|y|yes|1)$", Pattern.CASE_INSENSITIVE);

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
    
    public static F<Type, Boolean> isEqualTo(final Class<?> clazz) {
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
    
    public static F<Type, Boolean> isAssignableTo(final Class<?> clazz) {
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
    
    public static Collection<?> collectionImplementationOf(Class<?> clazz) {
        Collection collection = null;
        // If it is a collection or list, use array-list as the implementation.
        if (clazz.equals(Collection.class) || clazz.equals(List.class)) {
            collection = Lists.newArrayList();
        }
        // If it is a set, fall back to using a linked hash-set as the implementation.
        else if (clazz.equals(Set.class)) {
            collection = Sets.newLinkedHashSet();
        }
        // If it is a sorted set, fall back to using a tree-set as the implementation.
        else if (clazz.equals(SortedSet.class)) {
            collection = Sets.newTreeSet();
        }
        // If it is a queue, fall back to using a linked-list.
        else if (clazz.equals(Queue.class)) {
            collection = Lists.newLinkedList();
        }
        // Else, create implementation by calling constructor via reflection.
        else {
            collection = (Collection)implementationOf(clazz);
        }
        return collection;
    }
    
    public static Map<?, ?> mapImplementationOf(Class<?> clazz) {
        Map map = null;
        // If it is a map, use linked hash-map as the implementation.
        if (clazz.equals(Map.class)) {
            map = Maps.newLinkedHashMap();
        }
        // If it is a sorted map, fall back to using a tree-map as the implementation.
        else if (clazz.equals(SortedMap.class)) {
            map = Maps.newTreeMap();
        }
        // Else, create implementation by calling constructor via reflection.
        else {
            map = (Map)implementationOf(clazz);
        }
        return map;
    }
    
    public static Object implementationOf(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (InstantiationException exception) {
            throw new RuntimeException(exception);
        } catch (IllegalAccessException exception) {
            throw new RuntimeException(exception);
        }
    }
}

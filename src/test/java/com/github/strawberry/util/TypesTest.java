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

import com.google.inject.TypeLiteral;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import static com.github.strawberry.util.Types.genericTypeOf;
import static com.github.strawberry.util.Types.genericTypesOf;
import static com.github.strawberry.util.Types.isAssignableTo;
import static com.github.strawberry.util.Types.isEqualTo;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 *
 * @author Wiehann Matthysen
 */
public class TypesTest {
    
    private static class Dummy {
        public String string;
        public Integer integer;
        public List<String> stringList;
        public ArrayList<Integer> integerList;
        public Map<String, Integer> stringToIntMap;
        public HashMap<String, Map<Integer, Double>> stringToIntToDoubleMap;
    }
    
    @Test
    public void test_that_generic_types_are_correctly_identified_from_field() {
        Field[] fields = Dummy.class.getDeclaredFields();
        
        // String and Integer field doesn't have any generic parameters, thus empty array should be returned.
        assertThat(genericTypesOf(fields[0]).isEmpty(), is(true));
        assertThat(genericTypesOf(fields[1]).isEmpty(), is(true));
        
        // List<String> field should have single generic type parameter, namely: String.
        assertThat(genericTypesOf(fields[2]).length(), is(1));
        assertThat(genericTypesOf(fields[2]).get(0), is(equalTo((Type)String.class)));
        
        // List<Integer> field should have single generic type parameter, namely: Integer.
        assertThat(genericTypesOf(fields[3]).length(), is(1));
        assertThat(genericTypesOf(fields[3]).get(0), is(equalTo((Type)Integer.class)));
        
        // Map<String, Integer> field should have String and Integer generic parameters.
        assertThat(genericTypesOf(fields[4]).length(), is(2));
        assertThat(genericTypesOf(fields[4]).get(0), is(equalTo((Type)String.class)));
        assertThat(genericTypesOf(fields[4]).get(1), is(equalTo((Type)Integer.class)));
        
        // Map<String, Map<Integer, Double>> field should have String and Map<Integer, Double> generic parameters.
        assertThat(genericTypesOf(fields[5]).length(), is(2));
        assertThat(genericTypesOf(fields[5]).get(0), is(equalTo((Type)String.class)));
        assertThat(genericTypesOf(fields[5]).get(1), is(equalTo((new TypeLiteral<Map<Integer, Double>>(){}).getType())));
    }
    
    @Test
    public void test_that_generic_type_is_correctly_identified_from_field_via_index() {
        Field[] fields = Dummy.class.getDeclaredFields();
        
        // String field doesn't have any generic parameters.
        assertThat(genericTypeOf(fields[0], -1).isSome(), is(false));
        assertThat(genericTypeOf(fields[0], 0).isSome(), is(false));
        assertThat(genericTypeOf(fields[0], 1).isSome(), is(false));
        
        // Integer field doesn't have any generic parameters.
        assertThat(genericTypeOf(fields[1], -1).isSome(), is(false));
        assertThat(genericTypeOf(fields[1], 0).isSome(), is(false));
        assertThat(genericTypeOf(fields[1], 1).isSome(), is(false));
        
        // List<String> field should have one String generic parameter, at index 0.
        assertThat(genericTypeOf(fields[2], -1).isSome(), is(false));
        assertThat(genericTypeOf(fields[2], 0).isSome(), is(true));
        assertThat(genericTypeOf(fields[2], 0).some(), is(equalTo((Type)String.class)));
        assertThat(genericTypeOf(fields[2], 1).isSome(), is(false));
        
        // List<Integer> field should have one Integer generic parameter at index 0.
        assertThat(genericTypeOf(fields[3], -1).isSome(), is(false));
        assertThat(genericTypeOf(fields[3], 0).isSome(), is(true));
        assertThat(genericTypeOf(fields[3], 0).some(), is(equalTo((Type)Integer.class)));
        assertThat(genericTypeOf(fields[3], 1).isSome(), is(false));
        
        // Map<String, Integer> field should have String and Integer generic parameters at index 0 and 1.
        assertThat(genericTypeOf(fields[4], -1).isSome(), is(false));
        assertThat(genericTypeOf(fields[4], 0).isSome(), is(true));
        assertThat(genericTypeOf(fields[4], 0).some(), is(equalTo((Type)String.class)));
        assertThat(genericTypeOf(fields[4], 1).isSome(), is(true));
        assertThat(genericTypeOf(fields[4], 1).some(), is(equalTo((Type)Integer.class)));
        assertThat(genericTypeOf(fields[4], 2).isSome(), is(false));
        
        // Map<String, Map<Integer, Double>> field should have
        // String and Map<Integer, Double> generic parameters at index 0 and 1.
        assertThat(genericTypeOf(fields[5], -1).isSome(), is(false));
        assertThat(genericTypeOf(fields[5], 0).isSome(), is(true));
        assertThat(genericTypeOf(fields[5], 0).some(), is(equalTo((Type)String.class)));
        assertThat(genericTypeOf(fields[5], 1).isSome(), is(true));
        assertThat(genericTypeOf(fields[5], 1).some(), is(equalTo((new TypeLiteral<Map<Integer, Double>>(){}).getType())));
        assertThat(genericTypeOf(fields[5], 2).isSome(), is(false));
    }
    
    @Test
    public void test_that_type_is_correctly_compared_with_class_for_equality() {
        Field[] fields = Dummy.class.getDeclaredFields();
        
        // String field type should be equal to String.class
        assertThat(isEqualTo(String.class).f(fields[0].getType()), is(true));
        assertThat(isEqualTo(Integer.class).f(fields[0].getType()), is(false));
        
        // Integer field type should be equal to Integer.class
        assertThat(isEqualTo(Integer.class).f(fields[1].getType()), is(true));
        assertThat(isEqualTo(String.class).f(fields[1].getType()), is(false));
        
        // List generic field types should fall back to raw type when comparing of equality.
        assertThat(isEqualTo(List.class).f(fields[2].getType()), is(true));
        assertThat(isEqualTo(String.class).f(fields[2].getType()), is(false));
        assertThat(isEqualTo(ArrayList.class).f(fields[3].getType()), is(true));
        assertThat(isEqualTo(Integer.class).f(fields[3].getType()), is(false));
        
        // Map generic field type should fall back to raw type when comparing for equality.
        assertThat(isEqualTo(Map.class).f(fields[4].getType()), is(true));
        assertThat(isEqualTo(String.class).f(fields[4].getType()), is(false));
        assertThat(isEqualTo(Integer.class).f(fields[4].getType()), is(false));
        
        // Map generic field type should fall back to raw type when comparing for equality.
        assertThat(isEqualTo(HashMap.class).f(fields[5].getType()), is(true));
        assertThat(isEqualTo(String.class).f(fields[5].getType()), is(false));
        assertThat(isEqualTo(Integer.class).f(fields[5].getType()), is(false));
        assertThat(isEqualTo(Double.class).f(fields[5].getType()), is(false));
    }
    
    @Test
    public void test_that_type_is_correctly_compared_for_subtype_relationship() {
        Field[] fields = Dummy.class.getDeclaredFields();
        
        // String field type should be assignable to Object and String.
        assertThat(isAssignableTo(Object.class).f(fields[0].getType()), is(true));
        assertThat(isAssignableTo(String.class).f(fields[0].getType()), is(true));
        assertThat(isAssignableTo(Integer.class).f(fields[0].getType()), is(false));
        
        // Integer field type should be assignable to Object, Number and Integer.
        assertThat(isAssignableTo(Object.class).f(fields[1].getType()), is(true));
        assertThat(isAssignableTo(Number.class).f(fields[1].getType()), is(true));
        assertThat(isAssignableTo(Integer.class).f(fields[1].getType()), is(true));
        assertThat(isAssignableTo(String.class).f(fields[1].getType()), is(false));
        
        // List generic field types should be assignable to List.
        assertThat(isAssignableTo(Object.class).f(fields[2].getType()), is(true));
        assertThat(isAssignableTo(Collection.class).f(fields[2].getType()), is(true));
        assertThat(isAssignableTo(List.class).f(fields[2].getType()), is(true));
        assertThat(isAssignableTo(String.class).f(fields[2].getType()), is(false));
        assertThat(isAssignableTo(Object.class).f(fields[3].getType()), is(true));
        assertThat(isAssignableTo(List.class).f(fields[3].getType()), is(true));
        assertThat(isAssignableTo(ArrayList.class).f(fields[3].getType()), is(true));
        assertThat(isAssignableTo(Collection.class).f(fields[3].getType()), is(true));
        assertThat(isAssignableTo(Integer.class).f(fields[3].getType()), is(false));
        
        // Map generic field types should be assignable to Map.
        assertThat(isAssignableTo(Object.class).f(fields[4].getType()), is(true));
        assertThat(isAssignableTo(Map.class).f(fields[4].getType()), is(true));
        assertThat(isAssignableTo(String.class).f(fields[4].getType()), is(false));
        assertThat(isAssignableTo(Integer.class).f(fields[4].getType()), is(false));
        
        assertThat(isAssignableTo(Object.class).f(fields[5].getType()), is(true));
        assertThat(isAssignableTo(Map.class).f(fields[5].getType()), is(true));
        assertThat(isAssignableTo(HashMap.class).f(fields[5].getType()), is(true));
        assertThat(isAssignableTo(String.class).f(fields[5].getType()), is(false));
        assertThat(isAssignableTo(Integer.class).f(fields[5].getType()), is(false));
        assertThat(isAssignableTo(Double.class).f(fields[5].getType()), is(false));
    }
}

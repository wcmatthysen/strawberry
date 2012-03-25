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

import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import static com.github.strawberry.util.JedisUtil.destroyOnShutdown;

/**
 *
 * @author Wiehann Matthysen
 */
public class CharInjectionTest extends AbstractModule {

    private final JedisPool pool = destroyOnShutdown(new JedisPool("localhost", 6379));

    private Injector injector;
    private Jedis jedis;

    @Override
    protected void configure() {
        install(new RedisModule(this.pool));
    }

    @Before
    public void setup() {
        this.injector = Guice.createInjector(this);
        this.jedis = this.pool.getResource();
    }

    @After
    public void teardown() {
        for (String key : this.jedis.keys("test:*")) {
            this.jedis.del(key);
        }
        this.pool.returnResource(this.jedis);
    }



    public static class PrimitiveCharArrayContainer {

        @Redis(value = "test:chars", allowNull = false)
        private char[] injectedChars;

        public char[] getInjectedChars() {
            return this.injectedChars;
        }
    }

    public static class PrimitiveCharArrayAllowNullContainer {

        @Redis("test:chars")
        private char[] injectedChars;

        public char[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    public static class PrimitiveCharArrayDefaultValueContainer {

        @Redis("test:chars")
        private char[] injectedChars = {'t', 'e', 's', 't'};

        public char[] getInjectedChars() {
            return this.injectedChars;
        }
    }

    @Test
    public void test_that_string_is_injected_into_primitive_char_array() {
        this.jedis.set("test:chars", "test_value");
        PrimitiveCharArrayContainer dummy = this.injector.getInstance(
            PrimitiveCharArrayContainer.class);
        assertThat(dummy.getInjectedChars(), is(equalTo("test_value".toCharArray())));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_primitive_char_array() {
        PrimitiveCharArrayAllowNullContainer dummy = this.injector.getInstance(
            PrimitiveCharArrayAllowNullContainer.class);
        assertThat(dummy.getInjectedChars(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_primitive_char_array() {
        PrimitiveCharArrayContainer dummy = this.injector.getInstance(
            PrimitiveCharArrayContainer.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new char[]{})));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_char_array() {
        // Test for case where no value is present in redis database.
        PrimitiveCharArrayDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveCharArrayDefaultValueContainer.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new char[]{'t', 'e', 's', 't'})));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:chars", "test_value");
        dummy = this.injector.getInstance(PrimitiveCharArrayDefaultValueContainer.class);
        assertThat(dummy.getInjectedChars(), is(equalTo("test_value".toCharArray())));
    }



    public static class CharArrayContainer {

        @Redis(value = "test:chars", allowNull = false)
        private Character[] injectedChars;

        public Character[] getInjectedChars() {
            return this.injectedChars;
        }
    }

    public static class CharArrayAllowNullContainer {

        @Redis("test:chars")
        private Character[] injectedChars;

        public Character[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    public static class CharArrayDefaultValueContainer {

        @Redis("test:chars")
        private Character[] injectedChars = {'t', 'e', 's', 't'};

        public Character[] getInjectedChars() {
            return this.injectedChars;
        }
    }

    @Test
    public void test_that_string_is_injected_into_char_array() {
        this.jedis.set("test:chars", "test_value");
        CharArrayContainer dummy = this.injector.getInstance(CharArrayContainer.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedChars()), is(equalTo("test_value".toCharArray())));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_char_array() {
        CharArrayAllowNullContainer dummy = this.injector.getInstance(
            CharArrayAllowNullContainer.class);
        assertThat(dummy.getInjectedChars(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_char_array() {
        CharArrayContainer dummy = this.injector.getInstance(CharArrayContainer.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new Character[]{})));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_char_array() {
        // Test for case where no value is present in redis database.
        CharArrayDefaultValueContainer dummy = this.injector.getInstance(
            CharArrayDefaultValueContainer.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new Character[]{'t', 'e', 's', 't'})));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:chars", "test_value");
        dummy = this.injector.getInstance(CharArrayDefaultValueContainer.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedChars()), is(equalTo("test_value".toCharArray())));
    }



    public static class PrimitiveCharContainer {

        @Redis(value = "test:char", allowNull = false)
        private char injectedChar;

        public char getInjectedChar() {
            return this.injectedChar;
        }
    }

    public static class PrimitiveCharAllowNullContainer {

        @Redis(value = "test:char", forceUpdate = true)
        private char injectedChar;

        public char getInjectedChar() {
            return this.injectedChar;
        }
    }
    
    public static class PrimitiveCharDefaultValueContainer {

        @Redis("test:char")
        private char injectedChar = 't';

        public char getInjectedChar() {
            return this.injectedChar;
        }
    }

    @Test
    public void test_that_string_is_converted_into_primitive_char() {
        this.jedis.set("test:char", "A");
        PrimitiveCharContainer dummy = this.injector.getInstance(PrimitiveCharContainer.class);
        assertThat(dummy.getInjectedChar(), is('A'));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_char_to_null() {
        this.injector.getInstance(PrimitiveCharAllowNullContainer.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_character_into_primitive_char() {
        PrimitiveCharContainer dummy = this.injector.getInstance(PrimitiveCharContainer.class);
        assertThat(dummy.getInjectedChar(), is('\0'));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_char() {
        // Test for case where no value is present in redis database.
        PrimitiveCharDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveCharDefaultValueContainer.class);
        assertThat(dummy.getInjectedChar(), is('t'));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:char", "A");
        dummy = this.injector.getInstance(PrimitiveCharDefaultValueContainer.class);
        assertThat(dummy.getInjectedChar(), is('A'));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_char() {
        this.jedis.set("test:char", "invalid");
        this.injector.getInstance(PrimitiveCharContainer.class);
    }



    public static class CharContainer {

        @Redis(value = "test:char", allowNull = false)
        private Character injectedChar;

        public Character getInjectedChar() {
            return this.injectedChar;
        }
    }

    public static class CharAllowNullContainer {

        @Redis("test:char")
        private Character injectedChar;

        public Character getInjectedChar() {
            return this.injectedChar;
        }
    }
    
    public static class CharDefaultValueContainer {

        @Redis("test:char")
        private Character injectedChar = 't';

        public Character getInjectedChar() {
            return this.injectedChar;
        }
    }

    @Test
    public void test_that_string_is_converted_into_char() {
        this.jedis.set("test:char", "A");
        CharContainer dummy = this.injector.getInstance(CharContainer.class);
        assertThat(dummy.getInjectedChar(), is('A'));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_char() {
        CharAllowNullContainer dummy = this.injector.getInstance(
            CharAllowNullContainer.class);
        assertThat(dummy.getInjectedChar(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_char() {
        CharContainer dummy = this.injector.getInstance(CharContainer.class);
        assertThat(dummy.getInjectedChar(), is('\0'));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_char() {
        // Test for case where no value is present in redis database.
        CharDefaultValueContainer dummy = this.injector.getInstance(
            CharDefaultValueContainer.class);
        assertThat(dummy.getInjectedChar(), is('t'));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:char", "A");
        dummy = this.injector.getInstance(CharDefaultValueContainer.class);
        assertThat(dummy.getInjectedChar(), is('A'));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_char() {
        this.jedis.set("test:char", "invalid");
        this.injector.getInstance(CharContainer.class);
    }
}

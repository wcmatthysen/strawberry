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
package com.github.strawberry.guice.config;

import com.github.strawberry.guice.Config;
import com.github.strawberry.guice.ConfigModule;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import static com.github.strawberry.util.JedisUtil.destroyOnShutdown;
import java.util.Properties;

/**
 *
 * @author Wiehann Matthysen
 */
public class IntegerInjectionTest extends AbstractModule {

    Properties properties = new Properties();
    private Injector injector;

    @Override
    protected void configure() {
        install(new ConfigModule(this.properties));
    }

    @Before
    public void setup() {
        this.injector = Guice.createInjector(this);
    }

    @After
    public void teardown() {
    }



    public static class PrimitiveIntegerContainer {

        @Config(value = "test:integer", allowNull = false)
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    public static class PrimitiveIntegerAllowNullContainer {

        @Config(value = "test:integer", forceUpdate = true)
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class PrimitiveIntegerDefaultValueContainer {

        @Config("test:integer")
        private int injectedInteger = 123;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    @Test
    public void test_that_string_is_converted_into_primitive_integer() {
        properties.put("test:integer", "123");
        PrimitiveIntegerContainer dummy = this.injector.getInstance(PrimitiveIntegerContainer.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        properties.put("test:integer", "-123");
        dummy = this.injector.getInstance(PrimitiveIntegerContainer.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_integer_to_null() {
        this.injector.getInstance(PrimitiveIntegerAllowNullContainer.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_integer() {
        PrimitiveIntegerContainer dummy = this.injector.getInstance(
            PrimitiveIntegerContainer.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_integer() {
        // Test for case where no value is present in redis database.
        PrimitiveIntegerDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveIntegerDefaultValueContainer.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:integer", "456");
        dummy = this.injector.getInstance(PrimitiveIntegerDefaultValueContainer.class);
        assertThat(dummy.getInjectedInteger(), is(456));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_integer() {
        properties.put("test:integer", "invalid");
        this.injector.getInstance(PrimitiveIntegerContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_primitive_integer() {
        properties.put("test:integer", "-2147483649");
        this.injector.getInstance(PrimitiveIntegerContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_primitive_integer() {
        properties.put("test:integer", "2147483648");
        this.injector.getInstance(PrimitiveIntegerContainer.class);
    }



    public static class IntegerContainer {

        @Config(value = "test:integer", allowNull = false)
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    public static class IntegerAllowNullContainer {

        @Config("test:integer")
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class IntegerDefaultValueContainer {

        @Config("test:integer")
        private Integer injectedInteger = 123;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    @Test
    public void test_that_string_is_converted_into_integer() {
        properties.put("test:integer", "123");
        IntegerContainer dummy = this.injector.getInstance(IntegerContainer.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        properties.put("test:integer", "-123");
        dummy = this.injector.getInstance(IntegerContainer.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_integer() {
        IntegerAllowNullContainer dummy = this.injector.getInstance(
            IntegerAllowNullContainer.class);
        assertThat(dummy.getInjectedInteger(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_integer() {
        IntegerContainer dummy = this.injector.getInstance(IntegerContainer.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_integer() {
        // Test for case where no value is present in redis database.
        IntegerDefaultValueContainer dummy = this.injector.getInstance(
            IntegerDefaultValueContainer.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:integer", "456");
        dummy = this.injector.getInstance(IntegerDefaultValueContainer.class);
        assertThat(dummy.getInjectedInteger(), is(456));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_integer() {
        properties.put("test:integer", "invalid");
        this.injector.getInstance(IntegerContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_integer() {
        properties.put("test:integer", "-2147483649");
        this.injector.getInstance(IntegerContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_integer() {
        properties.put("test:integer", "2147483648");
        this.injector.getInstance(IntegerContainer.class);
    }
}

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
import static org.strawberry.util.JedisUtil.destroyOnShutdown;

/**
 *
 * @author Wiehann Matthysen
 */
public class IntegerInjectionTest extends AbstractModule {

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



    public static class PrimitiveIntegerWithoutKey {

        @Redis(value = "test:integer", allowNull = false)
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    public static class PrimitiveIntegerWithoutKeyAllowNull {

        @Redis(value = "test:integer", forceUpdate = true)
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class PrimitiveIntegerWithoutKeyDefaultValue {

        @Redis(value = "test:integer")
        private int injectedInteger = 123;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    @Test
    public void test_that_string_without_key_is_converted_into_primitive_integer() {
        this.jedis.set("test:integer", "123");
        PrimitiveIntegerWithoutKey dummy = this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_integer_to_null() {
        this.injector.getInstance(PrimitiveIntegerWithoutKeyAllowNull.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_integer() {
        PrimitiveIntegerWithoutKey dummy = this.injector.getInstance(
            PrimitiveIntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_integer() {
        // Test for case where no value is present in redis database.
        PrimitiveIntegerWithoutKeyDefaultValue dummy = this.injector.getInstance(
            PrimitiveIntegerWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:integer", "456");
        dummy = this.injector.getInstance(PrimitiveIntegerWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedInteger(), is(456));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_integer() {
        this.jedis.set("test:integer", "invalid");
        this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_primitive_integer() {
        this.jedis.set("test:integer", "-2147483649");
        this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_primitive_integer() {
        this.jedis.set("test:integer", "2147483648");
        this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
    }



    public static class IntegerWithoutKey {

        @Redis(value = "test:integer", allowNull = false)
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    public static class IntegerWithoutKeyAllowNull {

        @Redis("test:integer")
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class IntegerWithoutKeyDefaultValue {

        @Redis(value = "test:integer")
        private Integer injectedInteger = 123;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    @Test
    public void test_that_string_without_key_is_converted_into_integer() {
        this.jedis.set("test:integer", "123");
        IntegerWithoutKey dummy = this.injector.getInstance(IntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(IntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_integer() {
        IntegerWithoutKeyAllowNull dummy = this.injector.getInstance(
            IntegerWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedInteger(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_integer() {
        IntegerWithoutKey dummy = this.injector.getInstance(IntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_integer() {
        // Test for case where no value is present in redis database.
        IntegerWithoutKeyDefaultValue dummy = this.injector.getInstance(
            IntegerWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:integer", "456");
        dummy = this.injector.getInstance(IntegerWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedInteger(), is(456));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_integer() {
        this.jedis.set("test:integer", "invalid");
        this.injector.getInstance(IntegerWithoutKey.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_integer() {
        this.jedis.set("test:integer", "-2147483649");
        this.injector.getInstance(IntegerWithoutKey.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_integer() {
        this.jedis.set("test:integer", "2147483648");
        this.injector.getInstance(IntegerWithoutKey.class);
    }
}

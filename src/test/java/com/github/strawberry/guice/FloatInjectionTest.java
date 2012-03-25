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

/**
 *
 * @author Wiehann Matthysen
 */
public class FloatInjectionTest extends AbstractModule {

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



    public static class PrimitiveFloatContainer {

        @Redis(value = "test:float", allowNull = false)
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }

    public static class PrimitiveFloatAllowNullContainer {

        @Redis(value = "test:float", forceUpdate = true)
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class PrimitiveFloatDefaultValueContainer{

        @Redis("test:float")
        private float injectedFloat = 123.4f;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }

    @Test
    public void test_that_string_is_converted_into_primitive_float() {
        this.jedis.set("test:float", "123.456");
        PrimitiveFloatContainer dummy = this.injector.getInstance(PrimitiveFloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(123.456f));
        this.jedis.set("test:float", ".123");
        dummy = this.injector.getInstance(PrimitiveFloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(0.123f));
        this.jedis.set("test:float", "1.23f");
        dummy = this.injector.getInstance(PrimitiveFloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(1.23f));
        this.jedis.set("test:float", "NaN");
        dummy = this.injector.getInstance(PrimitiveFloatContainer.class);
        assertThat(Float.isNaN(dummy.getInjectedFloat()), is(true));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_float_to_null() {
        this.injector.getInstance(PrimitiveFloatAllowNullContainer.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_float() {
        PrimitiveFloatContainer dummy = this.injector.getInstance(
            PrimitiveFloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(0.0f));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_float() {
        // Test for case where no value is present in redis database.
        PrimitiveFloatDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveFloatDefaultValueContainer.class);
        assertThat(dummy.getInjectedFloat(), is(123.4f));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:float", "4.56");
        dummy = this.injector.getInstance(PrimitiveFloatDefaultValueContainer.class);
        assertThat(dummy.getInjectedFloat(), is(4.56f));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_float() {
        this.jedis.set("test:float", "invalid");
        this.injector.getInstance(PrimitiveFloatContainer.class);
    }

    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_primitive_float() {
        this.jedis.set("test:float", "-3.4028236e+38f");
        PrimitiveFloatContainer dummy = this.injector.getInstance(
            PrimitiveFloatContainer.class);
        assertThat(Float.isInfinite(dummy.getInjectedFloat()), is(true));
    }

    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_primitive_float() {
        this.jedis.set("test:float", "3.4028236e+38f");
        PrimitiveFloatContainer dummy = this.injector.getInstance(
            PrimitiveFloatContainer.class);
        assertThat(Float.isInfinite(dummy.getInjectedFloat()), is(true));
    }



    public static class FloatContainer {

        @Redis(value = "test:float", allowNull = false)
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }

    public static class FloatAllowNullContainer {

        @Redis("test:float")
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class FloatDefaultValueContainer {

        @Redis("test:float")
        private Float injectedFloat = 123.4f;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }

    @Test
    public void test_that_string_is_converted_into_float() {
        this.jedis.set("test:float", "123.456");
        FloatContainer dummy = this.injector.getInstance(FloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(123.456f));
        this.jedis.set("test:float", ".123");
        dummy = this.injector.getInstance(FloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(0.123f));
        this.jedis.set("test:float", "1.23f");
        dummy = this.injector.getInstance(FloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(1.23f));
        this.jedis.set("test:float", "NaN");
        dummy = this.injector.getInstance(FloatContainer.class);
        assertThat(dummy.getInjectedFloat().isNaN(), is(true));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_float() {
        FloatAllowNullContainer dummy = this.injector.getInstance(
            FloatAllowNullContainer.class);
        assertThat(dummy.getInjectedFloat(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_float() {
        FloatContainer dummy = this.injector.getInstance(FloatContainer.class);
        assertThat(dummy.getInjectedFloat(), is(0.0f));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_float() {
        // Test for case where no value is present in redis database.
        FloatDefaultValueContainer dummy = this.injector.getInstance(
            FloatDefaultValueContainer.class);
        assertThat(dummy.getInjectedFloat(), is(123.4f));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:float", "4.56");
        dummy = this.injector.getInstance(FloatDefaultValueContainer.class);
        assertThat(dummy.getInjectedFloat(), is(4.56f));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_float() {
        this.jedis.set("test:float", "invalid");
        this.injector.getInstance(FloatContainer.class);
    }

    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_float() {
        this.jedis.set("test:float", "-3.4028236e+38f");
        FloatContainer dummy = this.injector.getInstance(FloatContainer.class);
        assertThat(dummy.getInjectedFloat().isInfinite(), is(true));
    }

    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_float() {
        this.jedis.set("test:float", "3.4028236e+38f");
        FloatContainer dummy = this.injector.getInstance(FloatContainer.class);
        assertThat(dummy.getInjectedFloat().isInfinite(), is(true));
    }
}

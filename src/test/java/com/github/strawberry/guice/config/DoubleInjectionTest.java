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
public class DoubleInjectionTest extends AbstractModule {

    Properties properties = new Properties();

    private Injector injector;

    @Override
    protected void configure() {
        install(new ConfigModule(properties));
    }

    @Before
    public void setup() {
        this.injector = Guice.createInjector(this);
    }

    @After
    public void teardown() {
    }



    public static class PrimitiveDoubleContainer {

        @Config(value = "test:double", allowNull = false)
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }

    public static class PrimitiveDoubleAllowNullContainer {

        @Config(value = "test:double", forceUpdate = true)
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class PrimitiveDoubleDefaultValueContainer {

        @Config("test:double")
        private double injectedDouble = 123.4;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }

    @Test
    public void test_that_string_is_converted_into_primitive_double() {
        properties.put("test:double", "123.456");
        PrimitiveDoubleContainer dummy = this.injector.getInstance(PrimitiveDoubleContainer.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        properties.put("test:double", ".123");
        dummy = this.injector.getInstance(PrimitiveDoubleContainer.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
        properties.put("test:double", "NaN");
        dummy = this.injector.getInstance(PrimitiveDoubleContainer.class);
        assertThat(Double.isNaN(dummy.getInjectedDouble()), is(true));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_double_to_null() {
        this.injector.getInstance(PrimitiveDoubleAllowNullContainer.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_double() {
        PrimitiveDoubleContainer dummy = this.injector.getInstance(
            PrimitiveDoubleContainer.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_double() {
        // Test for case where no value is present in redis database.
        PrimitiveDoubleDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveDoubleDefaultValueContainer.class);
        assertThat(dummy.getInjectedDouble(), is(123.4));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:double", "4.56");
        dummy = this.injector.getInstance(PrimitiveDoubleDefaultValueContainer.class);
        assertThat(dummy.getInjectedDouble(), is(4.56));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_double() {
        properties.put("test:double", "invalid");
        this.injector.getInstance(PrimitiveDoubleContainer.class);
    }

    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_primitive_double() {
        properties.put("test:double", "-1.7976931348623159e+308");
        PrimitiveDoubleContainer dummy = this.injector.getInstance(
            PrimitiveDoubleContainer.class);
        assertThat(Double.isInfinite(dummy.getInjectedDouble()), is(true));
    }

    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_primitive_double() {
        properties.put("test:double", "1.7976931348623159e+308");
        PrimitiveDoubleContainer dummy = this.injector.getInstance(
            PrimitiveDoubleContainer.class);
        assertThat(Double.isInfinite(dummy.getInjectedDouble()), is(true));
    }



    public static class DoubleContainer {

        @Config(value = "test:double", allowNull = false)
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }

    public static class DoubleAllowNullContainer {

        @Config("test:double")
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class DoubleDefaultValueContainer {

        @Config("test:double")
        private Double injectedDouble = 123.4;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }

    @Test
    public void test_that_string_is_converted_into_double() {
        properties.put("test:double", "123.456");
        DoubleContainer dummy = this.injector.getInstance(DoubleContainer.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        properties.put("test:double", ".123");
        dummy = this.injector.getInstance(DoubleContainer.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
        properties.put("test:double", "NaN");
        dummy = this.injector.getInstance(DoubleContainer.class);
        assertThat(dummy.getInjectedDouble().isNaN(), is(true));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_double() {
        DoubleAllowNullContainer dummy = this.injector.getInstance(
            DoubleAllowNullContainer.class);
        assertThat(dummy.getInjectedDouble(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_double() {
        DoubleContainer dummy = this.injector.getInstance(DoubleContainer.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_double() {
        // Test for case where no value is present in redis database.
        DoubleDefaultValueContainer dummy = this.injector.getInstance(
            DoubleDefaultValueContainer.class);
        assertThat(dummy.getInjectedDouble(), is(123.4));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:double", "4.56");
        dummy = this.injector.getInstance(DoubleDefaultValueContainer.class);
        assertThat(dummy.getInjectedDouble(), is(4.56));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_double() {
        properties.put("test:double", "invalid");
        this.injector.getInstance(DoubleContainer.class);
    }

    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_double() {
        properties.put("test:double", "-1.7976931348623159e+308");
        DoubleContainer dummy = this.injector.getInstance(DoubleContainer.class);
        assertThat(dummy.getInjectedDouble().isInfinite(), is(true));
    }

    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_double() {
        properties.put("test:double", "1.7976931348623159e+308");
        DoubleContainer dummy = this.injector.getInstance(DoubleContainer.class);
        assertThat(dummy.getInjectedDouble().isInfinite(), is(true));
    }
}

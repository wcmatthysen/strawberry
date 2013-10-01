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
public class ShortInjectionTest extends AbstractModule {

    private Injector injector;

    Properties properties = new Properties();

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



    public static class PrimitiveShortContainer {

        @Config(value = "test:short", allowNull = false)
        private short injectedShort;

        public short getInjectedShort() {
            return this.injectedShort;
        }
    }

    public static class PrimitiveShortAllowNullContainer {

        @Config(value = "test:short", forceUpdate = true)
        private short injectedShort;

        public short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    public static class PrimitiveShortDefaultValueContainer {

        @Config("test:short")
        private short injectedShort = 123;

        public short getInjectedShort() {
            return this.injectedShort;
        }
    }

    @Test
    public void test_that_string_is_converted_into_primitive_short() {
        properties.put("test:short", "123");
        PrimitiveShortContainer dummy = this.injector.getInstance(PrimitiveShortContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        properties.put("test:short", "-123");
        dummy = this.injector.getInstance(PrimitiveShortContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)-123));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_short_to_null() {
        this.injector.getInstance(PrimitiveShortAllowNullContainer.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_short() {
        PrimitiveShortContainer dummy = this.injector.getInstance(
            PrimitiveShortContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_short() {
        // Test for case where no value is present in redis database.
        PrimitiveShortDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveShortDefaultValueContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:short", "456");
        dummy = this.injector.getInstance(PrimitiveShortDefaultValueContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)456));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_short() {
        properties.put("test:short", "invalid");
        this.injector.getInstance(PrimitiveShortContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_primitive_short() {
        properties.put("test:short", "-32,769");
        this.injector.getInstance(PrimitiveShortContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_primitive_short() {
        properties.put("test:short", "32,768");
        this.injector.getInstance(PrimitiveShortContainer.class);
    }



    public static class ShortContainer {

        @Config(value = "test:short", allowNull = false)
        private Short injectedShort;

        public Short getInjectedShort() {
            return this.injectedShort;
        }
    }

    public static class ShortAllowNullContainer {

        @Config("test:short")
        private Short injectedShort;

        public Short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    public static class ShortDefaultValueContainer {

        @Config("test:short")
        private Short injectedShort = 123;

        public Short getInjectedShort() {
            return this.injectedShort;
        }
    }

    @Test
    public void test_that_string_is_converted_into_short() {
        properties.put("test:short", "123");
        ShortContainer dummy = this.injector.getInstance(ShortContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        properties.put("test:short", "-123");
        dummy = this.injector.getInstance(ShortContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)-123));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_short() {
        ShortAllowNullContainer dummy = this.injector.getInstance(
            ShortAllowNullContainer.class);
        assertThat(dummy.getInjectedShort(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_short() {
        ShortContainer dummy = this.injector.getInstance(ShortContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_short() {
        // Test for case where no value is present in redis database.
        ShortDefaultValueContainer dummy = this.injector.getInstance(
            ShortDefaultValueContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:short", "456");
        dummy = this.injector.getInstance(ShortDefaultValueContainer.class);
        assertThat(dummy.getInjectedShort(), is((short)456));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_short() {
        properties.put("test:short", "invalid");
        this.injector.getInstance(ShortContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_short() {
        properties.put("test:short", "-32,769");
        this.injector.getInstance(ShortContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_short() {
        properties.put("test:short", "32,768");
        this.injector.getInstance(ShortContainer.class);
    }
}

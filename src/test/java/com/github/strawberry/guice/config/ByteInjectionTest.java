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
import java.util.Properties;

/**
 *
 * @author Wiehann Matthysen
 */
public class ByteInjectionTest extends AbstractModule {

    private Injector injector;
    private Properties properties = new Properties();

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
        properties.clear();
    }



    public static class PrimitiveByteArrayContainer {

        @Config(value = "test:bytes", allowNull = false)
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }

    public static class PrimitiveByteArrayAllowNullContainer {

        @Config("test:bytes")
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class PrimitiveByteArrayDefaultValueContainer {

        @Config("test:bytes")
        private byte[] injectedBytes = {(byte)1, (byte)2, (byte)3};

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }

    @Test
    public void test_that_string_is_injected_into_primitive_byte_array() {
        properties.put("test:bytes", "test_value");
        PrimitiveByteArrayContainer dummy = this.injector.getInstance(
            PrimitiveByteArrayContainer.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo("test_value".getBytes())));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_primitive_byte_array() {
        PrimitiveByteArrayAllowNullContainer dummy = this.injector.getInstance(
            PrimitiveByteArrayAllowNullContainer.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_primitive_byte_array() {
        PrimitiveByteArrayContainer dummy = this.injector.getInstance(
            PrimitiveByteArrayContainer.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new byte[]{})));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_byte_array() {
        // Test for case where no value is present in redis database.
        PrimitiveByteArrayDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveByteArrayDefaultValueContainer.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new byte[]{(byte)1, (byte)2, (byte)3})));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:bytes", "test_value");
        dummy = this.injector.getInstance(PrimitiveByteArrayDefaultValueContainer.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo("test_value".getBytes())));
    }



    public static class ByteArrayContainer {

        @Config(value = "test:bytes", allowNull = false)
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }

    public static class ByteArrayAllowNullContainer {

        @Config("test:bytes")
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class ByteArrayDefaultValueContainer {

        @Config("test:bytes")
        private Byte[] injectedBytes = {(byte)1, (byte)2, (byte)3};

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }

    @Test
    public void test_that_string_is_injected_into_byte_array() {
        properties.put("test:bytes", "test_value");
        ByteArrayContainer dummy = this.injector.getInstance(ByteArrayContainer.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedBytes()), is(equalTo("test_value".getBytes())));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_byte_array() {
        ByteArrayAllowNullContainer dummy = this.injector.getInstance(
            ByteArrayAllowNullContainer.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_byte_array() {
        ByteArrayContainer dummy = this.injector.getInstance(
            ByteArrayContainer.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new Byte[]{})));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_byte_array() {
        // Test for case where no value is present in redis database.
        ByteArrayDefaultValueContainer dummy = this.injector.getInstance(
            ByteArrayDefaultValueContainer.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new Byte[]{(byte)1, (byte)2, (byte)3})));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:bytes", "test_value");
        dummy = this.injector.getInstance(ByteArrayDefaultValueContainer.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedBytes()), is(equalTo("test_value".getBytes())));
    }



    public static class PrimitiveByteContainer {

        @Config(value = "test:byte", allowNull = false)
        private byte injectedByte;

        public byte getInjectedByte() {
            return this.injectedByte;
        }
    }

    public static class PrimitiveByteAllowNullContainer {

        @Config(value = "test:byte", forceUpdate = true)
        private byte injectedByte;

        public byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    public static class PrimitiveByteDefaultValueContainer {

        @Config("test:byte")
        private byte injectedByte = (byte)12;

        public byte getInjectedByte() {
            return this.injectedByte;
        }
    }

    @Test
    public void test_that_string_is_converted_into_primitive_byte() {
        properties.put("test:byte", "12");
        PrimitiveByteContainer dummy = this.injector.getInstance(PrimitiveByteContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        properties.put("test:byte", "-13");
        dummy = this.injector.getInstance(PrimitiveByteContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_byte_to_null() {
        this.injector.getInstance(PrimitiveByteAllowNullContainer.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_byte() {
        PrimitiveByteContainer dummy = this.injector.getInstance(
            PrimitiveByteContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_byte() {
        // Test for case where no value is present in redis database.
        PrimitiveByteDefaultValueContainer dummy = this.injector.getInstance(
            PrimitiveByteDefaultValueContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:byte", "-13");
        dummy = this.injector.getInstance(PrimitiveByteDefaultValueContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_byte() {
        properties.put("test:byte", "invalid");
        this.injector.getInstance(PrimitiveByteContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_primitive_byte() {
        properties.put("test:byte", "-129");
        this.injector.getInstance(PrimitiveByteContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_primitive_byte() {
        properties.put("test:byte", "128");
        this.injector.getInstance(PrimitiveByteContainer.class);
    }



    public static class ByteContainer {

        @Config(value = "test:byte", allowNull = false)
        private Byte injectedByte;

        public Byte getInjectedByte() {
            return this.injectedByte;
        }
    }

    public static class ByteAllowNullContainer {

        @Config("test:byte")
        private Byte injectedByte;

        public Byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    public static class ByteDefaultValueContainer {

        @Config("test:byte")
        private Byte injectedByte = (byte)12;

        public Byte getInjectedByte() {
            return this.injectedByte;
        }
    }

    @Test
    public void test_that_string_is_converted_into_byte() {
        properties.put("test:byte", "12");
        ByteContainer dummy = this.injector.getInstance(ByteContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        properties.put("test:byte", "-13");
        dummy = this.injector.getInstance(ByteContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_byte() {
        ByteAllowNullContainer dummy = this.injector.getInstance(
            ByteAllowNullContainer.class);
        assertThat(dummy.getInjectedByte(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_byte() {
        ByteContainer dummy = this.injector.getInstance(ByteContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)0));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_byte() {
        // Test for case where no value is present in redis database.
        ByteDefaultValueContainer dummy = this.injector.getInstance(
            ByteDefaultValueContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        properties.put("test:byte", "-13");
        dummy = this.injector.getInstance(ByteDefaultValueContainer.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_byte() {
        properties.put("test:byte", "invalid");
        this.injector.getInstance(ByteContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_byte() {
        properties.put("test:byte", "-129");
        this.injector.getInstance(ByteContainer.class);
    }

    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_byte() {
        properties.put("test:byte", "128");
        this.injector.getInstance(ByteContainer.class);
    }
}

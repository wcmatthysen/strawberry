package org.strawberry.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 * @author Wiehann Matthysen
 */
public class ByteInjectionTest extends AbstractModule {
    
    private final JedisPool pool = new JedisPool("localhost", 6379);
    
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
    
    
    
    public static class PrimitiveByteArrayWithoutKey {

        @Redis("test:bytes")
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class PrimitiveByteArrayWithoutKeyAllowNull {

        @Redis(value = "test:bytes", allowNull = true)
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_primitive_byte_array() {
        this.jedis.set("test:bytes", "test_value");
        PrimitiveByteArrayWithoutKey dummy = this.injector.getInstance(
            PrimitiveByteArrayWithoutKey.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo("test_value".getBytes())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_primitive_byte_array() {
        PrimitiveByteArrayWithoutKeyAllowNull dummy = this.injector.getInstance(
            PrimitiveByteArrayWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_primitive_byte_array() {
        PrimitiveByteArrayWithoutKey dummy = this.injector.getInstance(
            PrimitiveByteArrayWithoutKey.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new byte[]{})));
    }
    
    
    
    public static class ByteArrayWithoutKey {

        @Redis("test:bytes")
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class ByteArrayWithoutKeyAllowNull {

        @Redis(value = "test:bytes", allowNull = true)
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_byte_array() {
        this.jedis.set("test:bytes", "test_value");
        ByteArrayWithoutKey dummy = this.injector.getInstance(ByteArrayWithoutKey.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedBytes()), is(equalTo("test_value".getBytes())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_byte_array() {
        ByteArrayWithoutKeyAllowNull dummy = this.injector.getInstance(
            ByteArrayWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_byte_array() {
        ByteArrayWithoutKey dummy = this.injector.getInstance(
            ByteArrayWithoutKey.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new Byte[]{})));
    }
    
    
    
    public static class PrimitiveByteWithoutKey {

        @Redis("test:byte")
        private byte injectedByte;

        public byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    public static class PrimitiveByteWithoutKeyAllowNull {

        @Redis(value = "test:byte", allowNull = true)
        private byte injectedByte;

        public byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_byte() {
        this.jedis.set("test:byte", "12");
        PrimitiveByteWithoutKey dummy = this.injector.getInstance(PrimitiveByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        this.jedis.set("test:byte", "-13");
        dummy = this.injector.getInstance(PrimitiveByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_byte_to_null() {
        this.injector.getInstance(PrimitiveByteWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_byte() {
        PrimitiveByteWithoutKey dummy = this.injector.getInstance(
            PrimitiveByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_byte() {
        this.jedis.set("test:byte", "invalid");
        this.injector.getInstance(PrimitiveByteWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_primitive_byte() {
        this.jedis.set("test:byte", "-129");
        this.injector.getInstance(PrimitiveByteWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_primitive_byte() {
        this.jedis.set("test:byte", "128");
        this.injector.getInstance(PrimitiveByteWithoutKey.class);
    }
    
    
    
    public static class ByteWithoutKey {

        @Redis("test:byte")
        private Byte injectedByte;

        public Byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    public static class ByteWithoutKeyAllowNull {

        @Redis(value = "test:byte", allowNull = true)
        private Byte injectedByte;

        public Byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_byte() {
        this.jedis.set("test:byte", "12");
        ByteWithoutKey dummy = this.injector.getInstance(ByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        this.jedis.set("test:byte", "-13");
        dummy = this.injector.getInstance(ByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_byte() {
        ByteWithoutKeyAllowNull dummy = this.injector.getInstance(
            ByteWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedByte(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_byte() {
        ByteWithoutKey dummy = this.injector.getInstance(ByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_byte() {
        this.jedis.set("test:byte", "invalid");
        this.injector.getInstance(ByteWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_byte() {
        this.jedis.set("test:byte", "-129");
        this.injector.getInstance(ByteWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_byte() {
        this.jedis.set("test:byte", "128");
        this.injector.getInstance(ByteWithoutKey.class);
    }
}

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
    
    
    
    public static class PrimitiveFloatWithoutKey {

        @Redis("test:float")
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class PrimitiveFloatWithoutKeyAllowNull {

        @Redis(value = "test:float", allowNull = true)
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_float() {
        this.jedis.set("test:float", "123.456");
        PrimitiveFloatWithoutKey dummy = this.injector.getInstance(PrimitiveFloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(123.456f));
        this.jedis.set("test:float", ".123");
        dummy = this.injector.getInstance(PrimitiveFloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.123f));
        this.jedis.set("test:float", "1.23f");
        dummy = this.injector.getInstance(PrimitiveFloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(1.23f));
        this.jedis.set("test:float", "NaN");
        dummy = this.injector.getInstance(PrimitiveFloatWithoutKey.class);
        assertThat(Float.isNaN(dummy.getInjectedFloat()), is(true));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_float_to_null() {
        this.injector.getInstance(PrimitiveFloatWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_float() {
        PrimitiveFloatWithoutKey dummy = this.injector.getInstance(
            PrimitiveFloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.0f));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_float() {
        this.jedis.set("test:float", "invalid");
        this.injector.getInstance(PrimitiveFloatWithoutKey.class);
    }
    
    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_primitive_float() {
        this.jedis.set("test:float", "-3.4028236e+38f");
        PrimitiveFloatWithoutKey dummy = this.injector.getInstance(
            PrimitiveFloatWithoutKey.class);
        assertThat(Float.isInfinite(dummy.getInjectedFloat()), is(true));
    }
    
    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_primitive_float() {
        this.jedis.set("test:float", "3.4028236e+38f");
        PrimitiveFloatWithoutKey dummy = this.injector.getInstance(
            PrimitiveFloatWithoutKey.class);
        assertThat(Float.isInfinite(dummy.getInjectedFloat()), is(true));
    }
    
    
    
    public static class FloatWithoutKey {

        @Redis("test:float")
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class FloatWithoutKeyAllowNull {

        @Redis(value = "test:float", allowNull = true)
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_float() {
        this.jedis.set("test:float", "123.456");
        FloatWithoutKey dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(123.456f));
        this.jedis.set("test:float", ".123");
        dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.123f));
        this.jedis.set("test:float", "1.23f");
        dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(1.23f));
        this.jedis.set("test:float", "NaN");
        dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat().isNaN(), is(true));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_float() {
        FloatWithoutKeyAllowNull dummy = this.injector.getInstance(
            FloatWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedFloat(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_float() {
        FloatWithoutKey dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.0f));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_float() {
        this.jedis.set("test:float", "invalid");
        this.injector.getInstance(FloatWithoutKey.class);
    }
    
    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_float() {
        this.jedis.set("test:float", "-3.4028236e+38f");
        FloatWithoutKey dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat().isInfinite(), is(true));
    }
    
    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_float() {
        this.jedis.set("test:float", "3.4028236e+38f");
        FloatWithoutKey dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat().isInfinite(), is(true));
    }
}

package org.strawberry.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;

/**
 *
 * @author Wiehann Matthysen
 */
public class DoubleInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class PrimitiveDoubleWithoutKey {

        @Redis("test:double")
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class PrimitiveDoubleWithoutKeyAllowNull {

        @Redis(value = "test:double", allowNull = true)
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_double() {
        this.jedis.set("test:double", "123.456");
        PrimitiveDoubleWithoutKey dummy = this.injector.getInstance(PrimitiveDoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        this.jedis.set("test:double", ".123");
        dummy = this.injector.getInstance(PrimitiveDoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
        this.jedis.set("test:double", "NaN");
        dummy = this.injector.getInstance(PrimitiveDoubleWithoutKey.class);
        assertThat(Double.isNaN(dummy.getInjectedDouble()), is(true));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_double_to_null() {
        this.injector.getInstance(PrimitiveDoubleWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_double() {
        PrimitiveDoubleWithoutKey dummy = this.injector.getInstance(
            PrimitiveDoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_double() {
        this.jedis.set("test:double", "invalid");
        this.injector.getInstance(PrimitiveDoubleWithoutKey.class);
    }
    
    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_primitive_double() {
        this.jedis.set("test:double", "-1.7976931348623159e+308");
        PrimitiveDoubleWithoutKey dummy = this.injector.getInstance(
            PrimitiveDoubleWithoutKey.class);
        assertThat(Double.isInfinite(dummy.getInjectedDouble()), is(true));
    }
    
    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_primitive_double() {
        this.jedis.set("test:double", "1.7976931348623159e+308");
        PrimitiveDoubleWithoutKey dummy = this.injector.getInstance(
            PrimitiveDoubleWithoutKey.class);
        assertThat(Double.isInfinite(dummy.getInjectedDouble()), is(true));
    }
    
    
    
    public static class DoubleWithoutKey {

        @Redis("test:double")
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class DoubleWithoutKeyAllowNull {

        @Redis(value = "test:double", allowNull = true)
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_double() {
        this.jedis.set("test:double", "123.456");
        DoubleWithoutKey dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        this.jedis.set("test:double", ".123");
        dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
        this.jedis.set("test:double", "NaN");
        dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble().isNaN(), is(true));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_double() {
        DoubleWithoutKeyAllowNull dummy = this.injector.getInstance(
            DoubleWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedDouble(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_double() {
        DoubleWithoutKey dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_double() {
        this.jedis.set("test:double", "invalid");
        this.injector.getInstance(DoubleWithoutKey.class);
    }
    
    @Test
    public void test_that_too_small_value_overflows_to_infinity_when_converting_to_double() {
        this.jedis.set("test:double", "-1.7976931348623159e+308");
        DoubleWithoutKey dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble().isInfinite(), is(true));
    }
    
    @Test
    public void test_that_too_large_value_overflows_to_infinity_when_converting_to_double() {
        this.jedis.set("test:double", "1.7976931348623159e+308");
        DoubleWithoutKey dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble().isInfinite(), is(true));
    }
}
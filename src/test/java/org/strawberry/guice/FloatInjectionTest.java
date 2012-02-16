/**
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

        @Redis(value = "test:float", allowNull = false)
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }

    public static class PrimitiveFloatWithoutKeyAllowNull {

        @Redis(value = "test:float", forceUpdate = true)
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class PrimitiveFloatWithoutKeyDefaultValue{

        @Redis(value = "test:float")
        private float injectedFloat = 123.4f;

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
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_float() {
        // Test for case where no value is present in redis database.
        PrimitiveFloatWithoutKeyDefaultValue dummy = this.injector.getInstance(
            PrimitiveFloatWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedFloat(), is(123.4f));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:float", "4.56");
        dummy = this.injector.getInstance(PrimitiveFloatWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedFloat(), is(4.56f));
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

        @Redis(value = "test:float", allowNull = false)
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }

    public static class FloatWithoutKeyAllowNull {

        @Redis("test:float")
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class FloatWithoutKeyDefaultValue{

        @Redis(value = "test:float")
        private Float injectedFloat = 123.4f;

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
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_float() {
        // Test for case where no value is present in redis database.
        FloatWithoutKeyDefaultValue dummy = this.injector.getInstance(
            FloatWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedFloat(), is(123.4f));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:float", "4.56");
        dummy = this.injector.getInstance(FloatWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedFloat(), is(4.56f));
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

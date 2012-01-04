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
import static org.strawberry.util.JedisUtil.destroyOnShutdown;

/**
 *
 * @author Wiehann Matthysen
 */
public class ShortInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class PrimitiveShortWithoutKey {

        @Redis("test:short")
        private short injectedShort;

        public short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    public static class PrimitiveShortWithoutKeyAllowNull {

        @Redis(value = "test:short", allowNull = true)
        private short injectedShort;

        public short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_short() {
        this.jedis.set("test:short", "123");
        PrimitiveShortWithoutKey dummy = this.injector.getInstance(PrimitiveShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        this.jedis.set("test:short", "-123");
        dummy = this.injector.getInstance(PrimitiveShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)-123));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_short_to_null() {
        this.injector.getInstance(PrimitiveShortWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_short() {
        PrimitiveShortWithoutKey dummy = this.injector.getInstance(
            PrimitiveShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_short() {
        this.jedis.set("test:short", "invalid");
        this.injector.getInstance(PrimitiveShortWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_primitive_short() {
        this.jedis.set("test:short", "-32,769");
        this.injector.getInstance(PrimitiveShortWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_primitive_short() {
        this.jedis.set("test:short", "32,768");
        this.injector.getInstance(PrimitiveShortWithoutKey.class);
    }
    
    
    
    public static class ShortWithoutKey {

        @Redis("test:short")
        private Short injectedShort;

        public Short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    public static class ShortWithoutKeyAllowNull {

        @Redis(value = "test:short", allowNull = true)
        private Short injectedShort;

        public Short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_short() {
        this.jedis.set("test:short", "123");
        ShortWithoutKey dummy = this.injector.getInstance(ShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        this.jedis.set("test:short", "-123");
        dummy = this.injector.getInstance(ShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)-123));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_short() {
        ShortWithoutKeyAllowNull dummy = this.injector.getInstance(
            ShortWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedShort(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_short() {
        ShortWithoutKey dummy = this.injector.getInstance(ShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_short() {
        this.jedis.set("test:short", "invalid");
        this.injector.getInstance(ShortWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_short() {
        this.jedis.set("test:short", "-32,769");
        this.injector.getInstance(ShortWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_short() {
        this.jedis.set("test:short", "32,768");
        this.injector.getInstance(ShortWithoutKey.class);
    }
}

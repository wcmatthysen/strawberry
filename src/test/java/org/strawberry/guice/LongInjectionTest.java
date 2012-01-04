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
public class LongInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class PrimitiveLongWithoutKey {

        @Redis("test:long")
        private long injectedLong;

        public long getInjectedLong() {
            return this.injectedLong;
        }
    }
    
    public static class PrimitiveLongWithoutKeyAllowNull {

        @Redis(value = "test:long", allowNull = true)
        private long injectedLong;

        public long getInjectedLong() {
            return this.injectedLong;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_long() {
        this.jedis.set("test:long", "123");
        PrimitiveLongWithoutKey dummy = this.injector.getInstance(PrimitiveLongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(123L));
        this.jedis.set("test:long", "-123");
        dummy = this.injector.getInstance(PrimitiveLongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(-123L));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_long_to_null() {
        this.injector.getInstance(PrimitiveLongWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_long() {
        PrimitiveLongWithoutKey dummy = this.injector.getInstance(
            PrimitiveLongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(0L));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_long() {
        this.jedis.set("test:long", "invalid");
        this.injector.getInstance(PrimitiveLongWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_primitive_long() {
        this.jedis.set("test:long", "-9,223,372,036,854,775,809");
        this.injector.getInstance(PrimitiveLongWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_primitive_long() {
        this.jedis.set("test:long", "9,223,372,036,854,775,808");
        this.injector.getInstance(PrimitiveLongWithoutKey.class);
    }
    
    
    
    public static class LongWithoutKey {

        @Redis("test:long")
        private Long injectedLong;

        public Long getInjectedLong() {
            return this.injectedLong;
        }
    }
    
    public static class LongWithoutKeyAllowNull {

        @Redis(value = "test:long", allowNull = true)
        private Long injectedLong;

        public Long getInjectedLong() {
            return this.injectedLong;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_long() {
        this.jedis.set("test:long", "123");
        LongWithoutKey dummy = this.injector.getInstance(LongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(123L));
        this.jedis.set("test:long", "-123");
        dummy = this.injector.getInstance(LongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(-123L));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_long() {
        LongWithoutKeyAllowNull dummy = this.injector.getInstance(
            LongWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedLong(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_long() {
        LongWithoutKey dummy = this.injector.getInstance(LongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(0L));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_long() {
        this.jedis.set("test:long", "invalid");
        this.injector.getInstance(LongWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_small_value_throws_exception_when_converting_to_long() {
        this.jedis.set("test:long", "-9,223,372,036,854,775,809");
        this.injector.getInstance(LongWithoutKey.class);
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_too_large_value_throws_exception_when_converting_to_long() {
        this.jedis.set("test:long", "9,223,372,036,854,775,808");
        this.injector.getInstance(LongWithoutKey.class);
    }
}

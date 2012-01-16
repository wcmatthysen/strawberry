package org.strawberry.guice;

import java.math.BigInteger;

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
public class BigIntegerInjectionTest extends AbstractModule {

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



    public static class BigIntegerWithoutKey {

        @Redis("test:biginteger")
        private BigInteger bigInteger;

        public BigInteger getInjectedBigInteger() {
            return this.bigInteger;
        }
    }

    public static class BigIntegerWithoutKeyAllowNull {

        @Redis(value = "test:biginteger", allowNull = true)
        private BigInteger bigInteger;

        public BigInteger getInjectedBigInteger() {
            return this.bigInteger;
        }
    }

    @Test
    public void test_that_string_without_key_is_converted_into_biginteger() {
        this.jedis.set("test:biginteger", "123");
        BigIntegerWithoutKey dummy = this.injector.getInstance(BigIntegerWithoutKey.class);
        assertThat(dummy.getInjectedBigInteger().intValue(), is(123));
        this.jedis.set("test:biginteger", "-123");
        dummy = this.injector.getInstance(BigIntegerWithoutKey.class);
        assertThat(dummy.getInjectedBigInteger().intValue(), is(-123));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_biginteger() {
        BigIntegerWithoutKeyAllowNull dummy = this.injector.getInstance(
            BigIntegerWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedBigInteger(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_zero_into_biginteger() {
        BigIntegerWithoutKey dummy = this.injector.getInstance(BigIntegerWithoutKey.class);
        assertThat(dummy.getInjectedBigInteger().intValue(), is(0));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_biginteger() {
        this.jedis.set("test:biginteger", "invalid");
        this.injector.getInstance(BigIntegerWithoutKey.class);
    }

    @Test
    public void test_that_too_small_value_doesnt_throw_exception_when_converting_to_biginteger() {
        // Test for less than smallest integer.
        this.jedis.set("test:biginteger", "-2147483649");
        this.injector.getInstance(BigIntegerWithoutKey.class);

        // Test for less than smallest long.
        this.jedis.set("test:biginteger", "-9223372036854775809");
        this.injector.getInstance(BigIntegerWithoutKey.class);
    }

    @Test
    public void test_that_too_large_value_doesnt_throw_exception_when_converting_to_biginteger() {
        // Test for greater than largest integer.
        this.jedis.set("test:biginteger", "2147483648");
        this.injector.getInstance(BigIntegerWithoutKey.class);

        // Test for greater than largest long.
        this.jedis.set("test:biginteger", "9223372036854775808");
        this.injector.getInstance(BigIntegerWithoutKey.class);
    }
}

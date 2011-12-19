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
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 * @author Wiehann Matthysen
 */
public class StringInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class StringWithoutKey {

        @Redis("test:string")
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class StringWithoutKeyAllowNull {

        @Redis(value = "test:string", allowNull = true)
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_string() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringWithoutKey dummy = this.injector.getInstance(StringWithoutKey.class);
        assertThat(dummy.getInjectedString(), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_string() {
        StringWithoutKeyAllowNull dummy = this.injector.getInstance(
            StringWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedString(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_blank_into_string() {
        StringWithoutKey dummy = this.injector.getInstance(StringWithoutKey.class);
        assertThat(dummy.getInjectedString(), is(equalTo("")));
    }
}

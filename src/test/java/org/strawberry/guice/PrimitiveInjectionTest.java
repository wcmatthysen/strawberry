package org.strawberry.guice;

import com.google.common.collect.Iterables;
import java.util.Set;
import com.google.inject.Guice;
import org.junit.After;
import org.junit.Before;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 *
 * @author Wiehann Matthysen
 */
public class PrimitiveInjectionTest extends AbstractModule {

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
    
    
    
    public static class StringWithKey {

        @Redis(value = "test:string", includeKeys = true)
        private Map<String, String> injectedString;

        public Map<String, String> getInjectedString() {
            return this.injectedString;
        }
    }

    public static class StringInListWithoutKey {

        @Redis("test:string")
        private List<String> injectedString;

        public List<String> getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class StringInSetWithoutKey {
        
        @Redis("test:string")
        private Set<String> injectedString;
        
        public Set<String> getInjectedString() {
            return this.injectedString;
        }
    }
    
    @Test
    public void test_that_string_with_key_is_injected_into_map() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringWithKey dummy = this.injector.getInstance(StringWithKey.class);
        Map<String, String> actualStringMap = dummy.getInjectedString();
        assertThat(actualStringMap.size(), is(1));
        assertThat(actualStringMap.get("test:string"), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_list() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringInListWithoutKey dummy = this.injector.getInstance(StringInListWithoutKey.class);
        List<String> actualStringList = dummy.getInjectedString();
        assertThat(actualStringList.size(), is(1));
        assertThat(actualStringList.get(0), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_set() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringInSetWithoutKey dummy = this.injector.getInstance(StringInSetWithoutKey.class);
        Set<String> actualStringSet = dummy.getInjectedString();
        assertThat(actualStringSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualStringSet), is(equalTo(expectedString)));
    }
}

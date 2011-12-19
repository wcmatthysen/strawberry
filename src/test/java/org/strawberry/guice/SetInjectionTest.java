package org.strawberry.guice;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class SetInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class SetWithoutKey {

        @Redis("test:set")
        private Set<String> injectedSet;

        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SetAsListWithoutKey {
        
        @Redis("test:set")
        private List<String> injectedList;
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }

    public static class SetWithKey {

        @Redis(value = "test:set", includeKeys = true)
        private Map<String, Set<String>> injectedSet;

        public Map<String, Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SetInListWithoutKey {
        
        @Redis(value = "test:set", alwaysNest = true)
        private List<Set<String>> injectedSet;
        
        public List<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }

    public static class SetInSetWithoutKey {

        @Redis(value = "test:set", alwaysNest = true)
        private Set<Set<String>> injectedSet;

        public Set<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }

    public static class SetWithoutKeyAllowNull {

        @Redis(value = "test:set", allowNull = true)
        private Set<String> injectedSet;

        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    @Test
    public void test_that_set_without_key_is_injected_into_set() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SetWithoutKey dummy = this.injector.getInstance(SetWithoutKey.class);
        assertThat(dummy.getInjectedSet(), is(equalTo(expectedSet)));
    }
    
    @Test
    public void test_that_set_without_key_is_injected_into_list() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SetAsListWithoutKey dummy = this.injector.getInstance(SetAsListWithoutKey.class);
        Set<String> actualSet = Sets.newHashSet(dummy.getInjectedList());
        assertThat(actualSet, is(equalTo(expectedSet)));
    }
    
    @Test
    public void test_that_set_with_key_is_injected_into_map_of_set() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SetWithKey dummy = this.injector.getInstance(SetWithKey.class);
        Map<String, Set<String>> actualMapSet = dummy.getInjectedSet();
        assertThat(actualMapSet.size(), is(1));
        assertThat(actualMapSet.get("test:set"), is(equalTo(expectedSet)));
    }
    
    @Test
    public void test_that_set_without_key_is_injected_into_list_of_set() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SetInListWithoutKey dummy = this.injector.getInstance(SetInListWithoutKey.class);
        List<Set<String>> actualListSet = dummy.getInjectedSet();
        assertThat(actualListSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualListSet), is(equalTo(expectedSet)));
    }
    
    @Test
    public void test_that_set_without_key_is_injected_into_set_of_set() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SetInSetWithoutKey dummy = this.injector.getInstance(SetInSetWithoutKey.class);
        Set<Set<String>> actualSetSet = dummy.getInjectedSet();
        assertThat(actualSetSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualSetSet), is(equalTo(expectedSet)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_set() {
        SetWithoutKeyAllowNull dummy = this.injector.getInstance(
            SetWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedSet(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_set_into_set() {
        SetWithoutKey dummy = this.injector.getInstance(SetWithoutKey.class);
        assertThat(dummy.getInjectedSet(), is(equalTo(Collections.EMPTY_SET)));
    }
}

package org.strawberry.guice;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.strawberry.util.JedisUtil.destroyOnShutdown;

/**
 *
 * @author Wiehann Matthysen
 */
public class MapInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class MapWithoutKey {

        @Redis(value = "test:map", allowNull = false)
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }

    public static class MapWithKey {

        @Redis(value = "test:map", includeKeys = true)
        private Map<String, Map<String, String>> injectedMap;

        public Map<String, Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }

    public static class MapInListWithoutKey {

        @Redis("test:map")
        private List<Map<String, String>> injectedMap;

        public List<Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class MapInSetWithoutKey {
        
        @Redis("test:map")
        private Set<Map<String, String>> injectedMap;
        
        public Set<Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }

    public static class MapWithoutKeyAllowNull {

        @Redis(value = "test:map", allowNull = true)
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    @Test
    public void test_that_map_without_key_is_injected_into_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapWithoutKey dummy = this.injector.getInstance(MapWithoutKey.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_map_with_key_is_injected_into_map_of_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapWithKey dummy = this.injector.getInstance(MapWithKey.class);
        Map<String, Map<String, String>> actualMapMap = dummy.getInjectedMap();
        assertThat(actualMapMap.size(), is(1));
        assertThat(actualMapMap.get("test:map"), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_map_without_key_is_injected_into_list_of_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapInListWithoutKey dummy = this.injector.getInstance(MapInListWithoutKey.class);
        List<Map<String, String>> actualMapList = dummy.getInjectedMap();
        assertThat(actualMapList.size(), is(1));
        assertThat(actualMapList.get(0), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_map_without_key_is_injected_into_set_of_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapInSetWithoutKey dummy = this.injector.getInstance(MapInSetWithoutKey.class);
        Set<Map<String, String>> actualMapSet = dummy.getInjectedMap();
        assertThat(actualMapSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualMapSet), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_map() {
        MapWithoutKeyAllowNull dummy = this.injector.getInstance(
            MapWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedMap(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_map_into_map() {
        MapWithoutKey dummy = this.injector.getInstance(MapWithoutKey.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(Collections.EMPTY_MAP)));
    }
}

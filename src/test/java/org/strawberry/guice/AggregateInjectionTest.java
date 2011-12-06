package org.strawberry.guice;

import java.util.Set;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
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
public class AggregateInjectionTest extends AbstractModule {
    
    private final JedisPool pool = new JedisPool("localhost", 6379);
    
    private Injector injector;
    private Jedis jedis;
    
    public static class MultiStringsWithoutKeysInjectClass {
        
        @Redis("test:string:*")
        private List<String> injectedStrings;
        
        public List<String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    public static class MultiStringsWithKeysInjectClass {
        
        @Redis(value = "test:string:*", includeKeys = true)
        private Map<String, String> injectedStrings;
        
        public Map<String, String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    
    
    public static class MultiMapsWithoutKeysInjectClass {
        
        @Redis("test:map:*")
        private List<Map<String, String>> injectedMaps;
        
        public List<Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    public static class MultiMapsWithKeysInjectClass {
        
        @Redis(value = "test:map:*", includeKeys = true)
        private Map<String, Map<String, String>> injectedMaps;
        
        public Map<String, Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    
    
    public static class MultiListsWithoutKeysInjectClass {
        
        @Redis("test:list:*")
        private List<List<String>> injectedLists;
        
        public List<List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    public static class MultiListsWithKeysInjectClass {
        
        @Redis(value = "test:list:*", includeKeys = true)
        private Map<String, List<String>> injectedLists;
        
        public Map<String, List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    
    
    public static class MultiSetsWithoutKeysInjectClass {
        
        @Redis("test:set:*")
        private List<Set<String>> injectedSets;
        
        public List<Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    public static class MultiSetsWithKeysInjectClass {
        
        @Redis(value = "test:set:*", includeKeys = true)
        private Map<String, Set<String>> injectedSets;
        
        public Map<String, Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    
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
        this.pool.returnResource(this.jedis);
    }

    @Test
    public void test_that_multiple_strings_without_keys_are_injected_into_list_field() {
        String testString = "test_value:%s";
        List<String> expectedList = Lists.newArrayList();
        for (int i = 0; i < 10; ++i) {
            this.jedis.set(String.format("test:string:%s", i), String.format(testString, i));
            expectedList.add(String.format(testString, i));
        }
        MultiStringsWithoutKeysInjectClass dummy = this.injector.getInstance(MultiStringsWithoutKeysInjectClass.class);
        assertThat(Sets.newHashSet(dummy.getInjectedStrings()), is(equalTo(Sets.newHashSet(expectedList))));
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:string:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_strings_with_keys_are_injected_into_map_field() {
        String testString = "test_value:%s";
        Map<String, String> expectedMap = Maps.newLinkedHashMap();
        for (int i = 0; i < 10; ++i) {
            this.jedis.set(String.format("test:string:%s", i), String.format(testString, i));
            expectedMap.put(String.format("test:string:%s", i), String.format(testString, i));
        }
        MultiStringsWithKeysInjectClass dummy = this.injector.getInstance(MultiStringsWithKeysInjectClass.class);
        assertThat(dummy.getInjectedStrings(), is(equalTo(expectedMap)));
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:string:%s", i));
        }
    }
    
    
    
    @Test
    public void test_that_multiple_maps_without_keys_are_injected_into_list_of_map_field() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:map:%s", i), testMap);
        }
        MultiMapsWithoutKeysInjectClass dummy = this.injector.getInstance(MultiMapsWithoutKeysInjectClass.class);
        List<Map<String, String>> actualMaps = dummy.getInjectedMaps();
        assertThat(actualMaps.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Map<String, String> expectedMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            assertThat(actualMaps.contains(expectedMap), is(true));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:map:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_maps_with_keys_are_injected_into_map_of_map_field() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:map:%s", i), testMap);
        }
        MultiMapsWithKeysInjectClass dummy = this.injector.getInstance(MultiMapsWithKeysInjectClass.class);
        Map<String, Map<String, String>> actualMaps = dummy.getInjectedMaps();
        assertThat(actualMaps.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Map<String, String> expectedMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            assertThat(actualMaps.get(String.format("test:map:%s", i)), is(equalTo(expectedMap)));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:map:%s", i));
        }
    }
    
    
    
    @Test
    public void test_that_multiple_lists_without_keys_are_injected_into_list_of_list_field() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        MultiListsWithoutKeysInjectClass dummy = this.injector.getInstance(MultiListsWithoutKeysInjectClass.class);
        List<List<String>> actualLists = dummy.getInjectedLists();
        assertThat(actualLists.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            List<String> expectedList = Lists.newArrayList(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i)
            );
            assertThat(actualLists.contains(expectedList), is(true));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:list:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_lists_with_keys_are_injected_into_map_of_list_field() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        MultiListsWithKeysInjectClass dummy = this.injector.getInstance(MultiListsWithKeysInjectClass.class);
        Map<String, List<String>> actualLists = dummy.getInjectedLists();
        assertThat(actualLists.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            List<String> expectedList = Lists.newArrayList(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i)
            );
            assertThat(actualLists.get(String.format("test:list:%s", i)), is(equalTo(expectedList)));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:list:%s", i));
        }
    }
    
    
    
    @Test
    public void test_that_multiple_sets_without_keys_are_injected_into_list_of_set_field() {
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                this.jedis.sadd(String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        MultiSetsWithoutKeysInjectClass dummy = this.injector.getInstance(MultiSetsWithoutKeysInjectClass.class);
        List<Set<String>> actualSets = dummy.getInjectedSets();
        assertThat(actualSets.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Set<String> expectedSet = Sets.newHashSet(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i)
            );
            assertThat(actualSets.contains(expectedSet), is(true));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:set:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_sets_with_keys_are_injected_into_map_of_set_field() {
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                this.jedis.sadd(String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        MultiSetsWithKeysInjectClass dummy = this.injector.getInstance(MultiSetsWithKeysInjectClass.class);
        Map<String, Set<String>> actualSets = dummy.getInjectedSets();
        assertThat(actualSets.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Set<String> expecteSet = Sets.newHashSet(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i)
            );
            assertThat(actualSets.get(String.format("test:set:%s", i)), is(equalTo(expecteSet)));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:set:%s", i));
        }
    }
}

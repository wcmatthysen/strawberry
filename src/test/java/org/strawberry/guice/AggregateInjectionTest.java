package org.strawberry.guice;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.strawberry.util.JedisUtil.destroyOnShutdown;

/**
 *
 * @author Wiehann Matthysen
 */
public class AggregateInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class StringsWithoutKeys {
        
        @Redis("test:string:*")
        private List<String> injectedStrings;
        
        public List<String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    public static class StringsWithKeys {
        
        @Redis(value = "test:string:*", includeKeys = true)
        private Map<String, String> injectedStrings;
        
        public Map<String, String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    @Test
    public void test_that_strings_without_keys_are_injected_into_list() {
        String testString = "test_value:%s";
        List<String> expectedList = Lists.newArrayList();
        for (int i = 0; i < 10; ++i) {
            this.jedis.set(String.format("test:string:%s", i), String.format(testString, i));
            expectedList.add(String.format(testString, i));
        }
        StringsWithoutKeys dummy = this.injector.getInstance(StringsWithoutKeys.class);
        assertThat(Sets.newHashSet(dummy.getInjectedStrings()), is(equalTo(Sets.newHashSet(expectedList))));
    }
    
    @Test
    public void test_that_strings_with_keys_are_injected_into_map() {
        String testString = "test_value:%s";
        Map<String, String> expectedMap = Maps.newLinkedHashMap();
        for (int i = 0; i < 10; ++i) {
            this.jedis.set(String.format("test:string:%s", i), String.format(testString, i));
            expectedMap.put(String.format("test:string:%s", i), String.format(testString, i));
        }
        StringsWithKeys dummy = this.injector.getInstance(StringsWithKeys.class);
        assertThat(dummy.getInjectedStrings(), is(equalTo(expectedMap)));
    }
    
    
    
    public static class MapsWithoutKeys {
        
        @Redis("test:map:*")
        private List<Map<String, String>> injectedMaps;
        
        public List<Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    public static class MapsWithKeys {
        
        @Redis(value = "test:map:*", includeKeys = true)
        private Map<String, Map<String, String>> injectedMaps;
        
        public Map<String, Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    @Test
    public void test_that_maps_without_keys_are_injected_into_list_of_map() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:map:%s", i), testMap);
        }
        MapsWithoutKeys dummy = this.injector.getInstance(MapsWithoutKeys.class);
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
    }
    
    @Test
    public void test_that_maps_with_keys_are_injected_into_map_of_map() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:map:%s", i), testMap);
        }
        MapsWithKeys dummy = this.injector.getInstance(MapsWithKeys.class);
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
    }
    
    
    
    public static class ListsWithoutKeys {
        
        @Redis("test:list:*")
        private List<List<String>> injectedLists;
        
        public List<List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    public static class ListsWithKeys {
        
        @Redis(value = "test:list:*", includeKeys = true)
        private Map<String, List<String>> injectedLists;
        
        public Map<String, List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    @Test
    public void test_that_lists_without_keys_are_injected_into_list_of_list() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        ListsWithoutKeys dummy = this.injector.getInstance(ListsWithoutKeys.class);
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
    }
    
    @Test
    public void test_that_lists_with_keys_are_injected_into_map_of_list() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        ListsWithKeys dummy = this.injector.getInstance(ListsWithKeys.class);
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
    }
    
    
    
    public static class SetsWithoutKeys {
        
        @Redis("test:set:*")
        private List<Set<String>> injectedSets;
        
        public List<Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    public static class SetsWithKeys {
        
        @Redis(value = "test:set:*", includeKeys = true)
        private Map<String, Set<String>> injectedSets;
        
        public Map<String, Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    @Test
    public void test_that_sets_without_keys_are_injected_into_list_of_set() {
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                this.jedis.sadd(String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        SetsWithoutKeys dummy = this.injector.getInstance(SetsWithoutKeys.class);
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
    }
    
    @Test
    public void test_that_sets_with_keys_are_injected_into_map_of_set() {
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                this.jedis.sadd(String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        SetsWithKeys dummy = this.injector.getInstance(SetsWithKeys.class);
        Map<String, Set<String>> actualSets = dummy.getInjectedSets();
        assertThat(actualSets.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Set<String> expectedSet = Sets.newHashSet(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i)
            );
            assertThat(actualSets.get(String.format("test:set:%s", i)), is(equalTo(expectedSet)));
        }
    }
    
    
    
    public static class OrderedSetsWithoutKeys {
        
        @Redis("test:zset:*")
        private List<Set<String>> injectedSets;
        
        public List<Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    public static class OrderedSetsWithKeys {
        
        @Redis(value = "test:zset:*", includeKeys = true)
        private Map<String, Set<String>> injectedSets;
        
        public Map<String, Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    @Test
    public void test_that_ordered_sets_without_keys_are_injected_into_list_of_set() {
        List<String> testList = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.zadd(String.format("test:zset:%s", i), i, String.format(testValue, i));
            }
        }
        OrderedSetsWithoutKeys dummy = this.injector.getInstance(OrderedSetsWithoutKeys.class);
        List<Set<String>> actualSets = dummy.getInjectedSets();
        assertThat(actualSets.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Set<String> expectedSet = Sets.newLinkedHashSet(
                Lists.newArrayList(
                    String.format("value_%s3", i),
                    String.format("value_%s2", i),
                    String.format("value_%s1", i)
                )
            );
            assertThat(actualSets.contains(expectedSet), is(true));
        }
    }
    
    @Test
    public void test_that_ordered_sets_with_keys_are_injected_into_map_of_set() {
        List<String> testList = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.zadd(String.format("test:zset:%s", i), i, String.format(testValue, i));
            }
        }
        OrderedSetsWithKeys dummy = this.injector.getInstance(OrderedSetsWithKeys.class);
        Map<String, Set<String>> actualSets = dummy.getInjectedSets();
        assertThat(actualSets.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Set<String> expectedSet = Sets.newLinkedHashSet(
                Lists.newArrayList(
                    String.format("value_%s3", i),
                    String.format("value_%s2", i),
                    String.format("value_%s1", i)
                )
            );
            assertThat(actualSets.get(String.format("test:zset:%s", i)), is(equalTo(expectedSet)));
        }
    }
    
    
    
    public static class HeterogeneousWithoutKeys {
        
        @Redis(value = "test:heterogeneous:*")
        private List<Object> injectedObjects;
        
        public List<Object> getInjectedObjects() {
            return this.injectedObjects;
        }
    }
    
    public static class HeterogeneousWithKeys {
        
        @Redis(value = "test:heterogeneous:*", includeKeys = true)
        private Map<String, Object> injectedObjects;
        
        public Map<String, Object> getInjectedObjects() {
            return this.injectedObjects;
        }
    }
    
    @Test
    public void test_that_objects_without_keys_are_injected_into_list_of_objects() {
        // Inject some strings.
        for (int i = 0; i < 3; ++i) {
            this.jedis.set(String.format("test:heterogeneous:%s", i), String.format("test_value:%s", i));
        }
        
        // Inject some maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:heterogeneous:%s", i), testMap);
        }
        
        // Inject some lists.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 6; i < 9; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some sets.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 9; i < 12; ++i) {
            for (String testValue : testSet) {
                this.jedis.sadd(String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some ordered sets.
        List<String> testOrderedSet = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 12; i < 15; ++i) {
            for (String testValue : testOrderedSet) {
                this.jedis.zadd(String.format("test:heterogeneous:%s", i), i, String.format(testValue, i));
            }
        }
        
        HeterogeneousWithoutKeys dummy = this.injector.getInstance(HeterogeneousWithoutKeys.class);
        List<Object> actualObjects = dummy.getInjectedObjects();
        assertThat(actualObjects.size(), is(15));
        
        // Test strings.
        for (int i = 0; i < 3; ++i) {
            assertThat(actualObjects.contains(String.format("test_value:%s", i)), is(true));
        }
        
        // Test maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> expectedMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            assertThat(actualObjects.contains(expectedMap), is(true));
        }
        
        // Test lists.
        for (int i = 6; i < 9; ++i) {
            List<String> expectedList = Lists.newArrayList(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i));
            assertThat(actualObjects.contains(expectedList), is(true));
        }
        
        // Test sets.
        for (int i = 9; i < 12; ++i) {
            Set<String> expectedSet = Sets.newHashSet(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i));
            assertThat(actualObjects.contains(expectedSet), is(true));
        }
        
        // Test ordered sets.
        for (int i = 12; i < 15; ++i) {
            Set<String> expectedSet = Sets.newLinkedHashSet(
                Lists.newArrayList(
                    String.format("value_%s3", i),
                    String.format("value_%s2", i),
                    String.format("value_%s1", i)
                )
            );
            assertThat(actualObjects.contains(expectedSet), is(true));
        }
    }
    
    @Test
    public void test_that_objects_with_keys_are_injected_into_map_of_objects() {
        // Inject some strings.
        for (int i = 0; i < 3; ++i) {
            this.jedis.set(String.format("test:heterogeneous:%s", i), String.format("test_value:%s", i));
        }
        
        // Inject some maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:heterogeneous:%s", i), testMap);
        }
        
        // Inject some lists.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 6; i < 9; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some sets.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 9; i < 12; ++i) {
            for (String testValue : testSet) {
                this.jedis.sadd(String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some ordered sets.
        List<String> testOrderedSet = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 12; i < 15; ++i) {
            for (String testValue : testOrderedSet) {
                this.jedis.zadd(String.format("test:heterogeneous:%s", i), i, String.format(testValue, i));
            }
        }
        
        HeterogeneousWithKeys dummy = this.injector.getInstance(HeterogeneousWithKeys.class);
        Map<String, Object> actualObjects = dummy.getInjectedObjects();
        assertThat(actualObjects.size(), is(15));
        
        // Test strings.
        for (int i = 0; i < 3; ++i) {
            String actualString = (String)actualObjects.get(String.format("test:heterogeneous:%s", i));
            assertThat(actualString, is(equalTo(String.format("test_value:%s", i))));
        }
        
        // Test maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> actualMap = (Map)actualObjects.get(String.format("test:heterogeneous:%s", i));
            Map<String, String> expectedMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            assertThat(actualMap, is(equalTo(expectedMap)));
        }
        
        // Test lists.
        for (int i = 6; i < 9; ++i) {
            List<String> actualList = (List)actualObjects.get(String.format("test:heterogeneous:%s", i));
            List<String> expectedList = Lists.newArrayList(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i));
            assertThat(actualList, is(equalTo(expectedList)));
        }
        
        // Test sets.
        for (int i = 9; i < 12; ++i) {
            Set<String> actualSet = (Set)actualObjects.get(String.format("test:heterogeneous:%s", i));
            Set<String> expectedSet = Sets.newHashSet(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i));
            assertThat(actualSet, is(equalTo(expectedSet)));
        }
        
        // Test ordered sets.
        for (int i = 12; i < 15; ++i) {
            Set<String> actualSet = (Set)actualObjects.get(String.format("test:heterogeneous:%s", i));
            Set<String> expectedSet = Sets.newLinkedHashSet(
                Lists.newArrayList(
                    String.format("value_%s3", i),
                    String.format("value_%s2", i),
                    String.format("value_%s1", i)
                )
            );
            assertThat(actualSet, is(equalTo(expectedSet)));
        }
    }
}

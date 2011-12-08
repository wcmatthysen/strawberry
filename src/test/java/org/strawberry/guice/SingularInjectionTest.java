package org.strawberry.guice;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
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
public class SingularInjectionTest extends AbstractModule {

    private final JedisPool pool = new JedisPool("localhost", 6379);
    
    private Injector injector;
    private Jedis jedis;

    public static class SingleMapWithoutKeyClass {

        @Redis("test:map")
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }

    public static class SingleMapWithKeyClass {

        @Redis(value = "test:map", includeKeys = true)
        private Map<String, Map<String, String>> injectedMap;

        public Map<String, Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }

    public static class SingleMapInListWithoutKeyClass {

        @Redis("test:map")
        private List<Map<String, String>> injectedMap;

        public List<Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class SingleMapInSetWithoutKeyClass {
        
        @Redis("test:map")
        private Set<Map<String, String>> injectedMap;
        
        public Set<Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }

    public static class SingleMapWithoutKeyMissingAllowNullClass {

        @Redis(value = "test:non_existent_map", allowNull = true)
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }

    public static class SingleMapWithoutKeyMissingClass {

        @Redis("test:non_existent_map")
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    
    
    public static class SingleListWithoutKeyClass {

        @Redis("test:list")
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class SingleListAsSetWithoutKeyClass {
        
        @Redis("test:list")
        private Set<String> injectedSet;
        
        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SingleListWithKeyClass {

        @Redis(value = "test:list", includeKeys = true)
        private Map<String, List<String>> injectedList;

        public Map<String, List<String>> getInjectedList() {
            return this.injectedList;
        }
    }

    public static class SingleListInListWithoutKeyClass {

        @Redis(value = "test:list", alwaysNest = true)
        private List<List<String>> injectedList;

        public List<List<String>> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class SingleListInSetWithoutKeyClass {
        
        @Redis(value = "test:list", alwaysNest = true)
        private Set<List<String>> injectedList;
        
        public Set<List<String>> getInjectedList() {
            return this.injectedList;
        }
    }

    public static class SingleListWithoutKeyMissingAllowNullClass {

        @Redis(value = "test:non_existent_list", allowNull = true)
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }

    public static class SingleListWithoutKeyMissingClass {

        @Redis(value = "test:non_existent_list")
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    

    public static class SingleSetWithoutKeyClass {

        @Redis("test:set")
        private Set<String> injectedSet;

        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SingleSetAsListWithoutKeyClass {
        
        @Redis("test:set")
        private List<String> injectedList;
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }

    public static class SingleSetWithKeyClass {

        @Redis(value = "test:set", includeKeys = true)
        private Map<String, Set<String>> injectedSet;

        public Map<String, Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SingleSetInListWithoutKeyClass {
        
        @Redis(value = "test:set", alwaysNest = true)
        private List<Set<String>> injectedSet;
        
        public List<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }

    public static class SingleSetInSetWithoutKeyClass {

        @Redis(value = "test:set", alwaysNest = true)
        private Set<Set<String>> injectedSet;

        public Set<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }

    public static class SingleSetWithoutKeyMissingAllowNullClass {

        @Redis(value = "test:non_existent_set", allowNull = true)
        private Set<String> injectedSet;

        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }

    public static class SingleSetWithoutKeyMissingClass {

        @Redis(value = "test:non_existent_set")
        private Set<String> injectedSet;

        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    
    
    public static class SingleOrderedSetWithoutKeyClass {
        
        @Redis("test:zset")
        private Set<String> injectedOrderedSet;

        public Set<String> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
    }
    
    public static class SingleOrderedSetAsListWithoutKeyClass {
        
        @Redis("test:zset")
        private List<String> injectedList;
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class SingleOrderedSetWithKeyClass {

        @Redis(value = "test:zset", includeKeys = true)
        private Map<String, Set<String>> injectedSet;

        public Map<String, Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SingleOrderedSetInListWithoutKeyClass {
        
        @Redis(value = "test:zset", alwaysNest = true)
        private List<Set<String>> injectedSet;
        
        public List<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SingleOrderedSetInSetWithoutKeyClass {

        @Redis(value = "test:zset", alwaysNest = true)
        private Set<Set<String>> injectedSet;

        public Set<Set<String>> getInjectedSet() {
            return this.injectedSet;
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
    public void test_that_single_map_without_key_is_injected_into_map_field() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        SingleMapWithoutKeyClass dummy = this.injector.getInstance(SingleMapWithoutKeyClass.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(expectedMap)));
        this.jedis.del("test:map");
    }
    
    @Test
    public void test_that_single_map_with_key_is_injected_into_map_field() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        SingleMapWithKeyClass dummy = this.injector.getInstance(SingleMapWithKeyClass.class);
        Map<String, Map<String, String>> actualMapMap = dummy.getInjectedMap();
        assertThat(actualMapMap.size(), is(1));
        assertThat(actualMapMap.get("test:map"), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_single_map_without_key_is_injected_into_list_field() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        SingleMapInListWithoutKeyClass dummy = this.injector.getInstance(SingleMapInListWithoutKeyClass.class);
        List<Map<String, String>> actualMapList = dummy.getInjectedMap();
        assertThat(actualMapList.size(), is(1));
        assertThat(actualMapList.get(0), is(equalTo(expectedMap)));
        this.jedis.del("test:map");
    }
    
    @Test
    public void test_that_single_map_without_key_is_injected_into_set_field() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        SingleMapInSetWithoutKeyClass dummy = this.injector.getInstance(SingleMapInSetWithoutKeyClass.class);
        Set<Map<String, String>> actualMapSet = dummy.getInjectedMap();
        assertThat(actualMapSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualMapSet), is(equalTo(expectedMap)));
        this.jedis.del("test:map");
    }
    
    @Test
    public void test_that_single_map_without_key_thats_missing_is_injected_as_null_into_map_field() {
        SingleMapWithoutKeyMissingAllowNullClass dummy = this.injector.getInstance(
            SingleMapWithoutKeyMissingAllowNullClass.class);
        assertThat(dummy.getInjectedMap(), is(nullValue()));
    }
    
    @Test
    public void test_that_single_map_without_key_thats_missing_is_injected_as_empty_map_into_map_field() {
        SingleMapWithoutKeyMissingClass dummy = this.injector.getInstance(SingleMapWithoutKeyMissingClass.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(Collections.EMPTY_MAP)));
    }
    
    
    
    @Test
    public void test_that_single_list_without_key_is_injected_into_list_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListWithoutKeyClass dummy = this.injector.getInstance(SingleListWithoutKeyClass.class);
        assertThat(dummy.getInjectedList(), is(equalTo(expectedList)));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_without_key_is_injected_into_set_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListAsSetWithoutKeyClass dummy = this.injector.getInstance(SingleListAsSetWithoutKeyClass.class);
        Set<String> actualSet = dummy.getInjectedSet();
        assertThat(actualSet, is(equalTo((Set)Sets.newHashSet(expectedList))));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_with_key_is_injected_into_map_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListWithKeyClass dummy = this.injector.getInstance(SingleListWithKeyClass.class);
        Map<String, List<String>> actualMapList = dummy.getInjectedList();
        assertThat(actualMapList.size(), is(1));
        assertThat(actualMapList.get("test:list"), is(equalTo(expectedList)));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_without_key_is_injected_into_list_of_list_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListInListWithoutKeyClass dummy = this.injector.getInstance(SingleListInListWithoutKeyClass.class);
        List<List<String>> actualListList = dummy.getInjectedList();
        assertThat(actualListList.size(), is(1));
        assertThat(actualListList.get(0), is(equalTo(expectedList)));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_witouth_key_is_injected_into_set_of_list_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListInSetWithoutKeyClass dummy = this.injector.getInstance(SingleListInSetWithoutKeyClass.class);
        Set<List<String>> actualSetList = dummy.getInjectedList();
        assertThat(actualSetList.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualSetList), is(equalTo(expectedList)));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_without_key_thats_missing_is_injected_as_null_into_list_field() {
        SingleListWithoutKeyMissingAllowNullClass dummy = this.injector.getInstance(
            SingleListWithoutKeyMissingAllowNullClass.class);
        assertThat(dummy.getInjectedList(), is(nullValue()));
    }
    
    @Test
    public void test_that_single_list_without_key_thats_missing_is_injected_as_empty_list_into_list_field() {
        SingleListWithoutKeyMissingClass dummy = this.injector.getInstance(SingleListWithoutKeyMissingClass.class);
        assertThat(dummy.getInjectedList(), is(equalTo(Collections.EMPTY_LIST)));
    }
    
    
    
    @Test
    public void test_that_single_set_without_key_is_injected_into_set_field() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SingleSetWithoutKeyClass dummy = this.injector.getInstance(SingleSetWithoutKeyClass.class);
        assertThat(dummy.getInjectedSet(), is(equalTo(expectedSet)));
        this.jedis.del("test:set");
    }
    
    @Test
    public void test_that_single_set_without_key_is_injected_into_list_field() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SingleSetAsListWithoutKeyClass dummy = this.injector.getInstance(SingleSetAsListWithoutKeyClass.class);
        Set<String> actualSet = Sets.newHashSet(dummy.getInjectedList());
        assertThat(actualSet, is(equalTo(expectedSet)));
        this.jedis.del("test:set");
    }
    
    @Test
    public void test_that_single_set_with_key_is_injected_into_map_field() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SingleSetWithKeyClass dummy = this.injector.getInstance(SingleSetWithKeyClass.class);
        Map<String, Set<String>> actualMapSet = dummy.getInjectedSet();
        assertThat(actualMapSet.size(), is(1));
        assertThat(actualMapSet.get("test:set"), is(equalTo(expectedSet)));
        this.jedis.del("test:set");
    }
    
    @Test
    public void test_that_single_set_without_key_is_injected_into_list_of_set_field() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SingleSetInListWithoutKeyClass dummy = this.injector.getInstance(SingleSetInListWithoutKeyClass.class);
        List<Set<String>> actualListSet = dummy.getInjectedSet();
        assertThat(actualListSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualListSet), is(equalTo(expectedSet)));
        this.jedis.del("test:set");
    }
    
    @Test
    public void test_that_single_set_without_key_is_injected_into_set_of_set_field() {
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        SingleSetInSetWithoutKeyClass dummy = this.injector.getInstance(SingleSetInSetWithoutKeyClass.class);
        Set<Set<String>> actualSetSet = dummy.getInjectedSet();
        assertThat(actualSetSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualSetSet), is(equalTo(expectedSet)));
        this.jedis.del("test:set");
    }
    
    @Test
    public void test_that_single_set_without_key_thats_missing_is_injected_as_null_into_set_field() {
        SingleSetWithoutKeyMissingAllowNullClass dummy = this.injector.getInstance(
            SingleSetWithoutKeyMissingAllowNullClass.class);
        assertThat(dummy.getInjectedSet(), is(nullValue()));
    }
    
    @Test
    public void test_that_single_set_without_key_thats_missing_is_injected_as_empty_set_into_set_field() {
        SingleSetWithoutKeyMissingClass dummy = this.injector.getInstance(SingleSetWithoutKeyMissingClass.class);
        assertThat(dummy.getInjectedSet(), is(equalTo(Collections.EMPTY_SET)));
    }
    
    
    
    @Test
    public void test_that_single_ordered_set_without_key_is_injected_into_set_field() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        SingleOrderedSetWithoutKeyClass dummy = this.injector.getInstance(SingleOrderedSetWithoutKeyClass.class);
        List<String> actualList = Lists.newArrayList(dummy.getInjectedOrderedSet());
        assertThat(actualList, is(equalTo(expectedList)));
        this.jedis.del("test:zset");
    }
    
    @Test
    public void test_that_single_ordered_set_without_key_is_injected_into_list_field() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        SingleOrderedSetAsListWithoutKeyClass dummy = this.injector.getInstance(SingleOrderedSetAsListWithoutKeyClass.class);
        List<String> actualList = dummy.getInjectedList();
        assertThat(actualList, is(equalTo(expectedList)));
        this.jedis.del("test:zset");
    }
    
    @Test
    public void test_that_single_ordered_set_with_key_is_injected_into_map_field() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        SingleOrderedSetWithKeyClass dummy = this.injector.getInstance(SingleOrderedSetWithKeyClass.class);
        Map<String, Set<String>> actualMapSet = dummy.getInjectedSet();
        assertThat(actualMapSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(actualMapSet.get("test:zset"));
        assertThat(actualList, is(equalTo(expectedList)));
        this.jedis.del("test:zset");
    }
    
    @Test
    public void test_that_single_ordered_set_without_key_is_injected_into_list_of_set_field() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        SingleOrderedSetInListWithoutKeyClass dummy = this.injector.getInstance(SingleOrderedSetInListWithoutKeyClass.class);
        List<Set<String>> actualListSet = dummy.getInjectedSet();
        assertThat(actualListSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(actualListSet.get(0));
        assertThat(actualList, is(equalTo(expectedList)));
        this.jedis.del("test:zset");
    }
    
    @Test
    public void test_that_single_ordered_set_without_key_is_injected_into_set_of_set_field() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        SingleOrderedSetInSetWithoutKeyClass dummy = this.injector.getInstance(SingleOrderedSetInSetWithoutKeyClass.class);
        Set<Set<String>> actualSetSet = dummy.getInjectedSet();
        assertThat(actualSetSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(Iterables.getOnlyElement(actualSetSet));
        assertThat(actualList, is(equalTo(expectedList)));
        this.jedis.del("test:zset");
    }
}

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

    public static class MapWithoutKey {

        @Redis("test:map")
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
    
    
    
    public static class ListWithoutKey {

        @Redis("test:list")
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class ListAsSetWithoutKey {
        
        @Redis("test:list")
        private Set<String> injectedSet;
        
        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class ListWithKey {

        @Redis(value = "test:list", includeKeys = true)
        private Map<String, List<String>> injectedList;

        public Map<String, List<String>> getInjectedList() {
            return this.injectedList;
        }
    }

    public static class ListInListWithoutKey {

        @Redis(value = "test:list", alwaysNest = true)
        private List<List<String>> injectedList;

        public List<List<String>> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class ListInSetWithoutKey {
        
        @Redis(value = "test:list", alwaysNest = true)
        private Set<List<String>> injectedList;
        
        public Set<List<String>> getInjectedList() {
            return this.injectedList;
        }
    }

    public static class ListWithoutKeyAllowNull {

        @Redis(value = "test:list", allowNull = true)
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
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
    
    
    
    public static class OrderedSetWithoutKey {
        
        @Redis("test:zset")
        private Set<String> injectedOrderedSet;

        public Set<String> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
    }
    
    public static class OrderedSetAsListWithoutKey {
        
        @Redis("test:zset")
        private List<String> injectedList;
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class OrderedSetWithKey {

        @Redis(value = "test:zset", includeKeys = true)
        private Map<String, Set<String>> injectedSet;

        public Map<String, Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class OrderedSetInListWithoutKey {
        
        @Redis(value = "test:zset", alwaysNest = true)
        private List<Set<String>> injectedSet;
        
        public List<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class OrderedSetInSetWithoutKey {

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
        for (String key : this.jedis.keys("test:*")) {
            this.jedis.del(key);
        }
        this.pool.returnResource(this.jedis);
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
    
    
    
    @Test
    public void test_that_list_without_key_is_injected_into_list() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        ListWithoutKey dummy = this.injector.getInstance(ListWithoutKey.class);
        assertThat(dummy.getInjectedList(), is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_list_without_key_is_injected_into_set() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        ListAsSetWithoutKey dummy = this.injector.getInstance(ListAsSetWithoutKey.class);
        Set<String> actualSet = dummy.getInjectedSet();
        assertThat(actualSet, is(equalTo((Set)Sets.newHashSet(expectedList))));
    }
    
    @Test
    public void test_that_list_with_key_is_injected_into_map_of_list() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        ListWithKey dummy = this.injector.getInstance(ListWithKey.class);
        Map<String, List<String>> actualMapList = dummy.getInjectedList();
        assertThat(actualMapList.size(), is(1));
        assertThat(actualMapList.get("test:list"), is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_list_without_key_is_injected_into_list_of_list() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        ListInListWithoutKey dummy = this.injector.getInstance(ListInListWithoutKey.class);
        List<List<String>> actualListList = dummy.getInjectedList();
        assertThat(actualListList.size(), is(1));
        assertThat(actualListList.get(0), is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_list_without_key_is_injected_into_set_of_list() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        ListInSetWithoutKey dummy = this.injector.getInstance(ListInSetWithoutKey.class);
        Set<List<String>> actualSetList = dummy.getInjectedList();
        assertThat(actualSetList.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualSetList), is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_list() {
        ListWithoutKeyAllowNull dummy = this.injector.getInstance(
            ListWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedList(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_list_into_list() {
        ListWithoutKey dummy = this.injector.getInstance(ListWithoutKey.class);
        assertThat(dummy.getInjectedList(), is(equalTo(Collections.EMPTY_LIST)));
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
    
    
    
    @Test
    public void test_that_ordered_set_without_key_is_injected_into_set() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetWithoutKey dummy = this.injector.getInstance(OrderedSetWithoutKey.class);
        List<String> actualList = Lists.newArrayList(dummy.getInjectedOrderedSet());
        assertThat(actualList, is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_ordered_set_without_key_is_injected_into_list() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetAsListWithoutKey dummy = this.injector.getInstance(OrderedSetAsListWithoutKey.class);
        List<String> actualList = dummy.getInjectedList();
        assertThat(actualList, is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_ordered_set_with_key_is_injected_into_map_of_set() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetWithKey dummy = this.injector.getInstance(OrderedSetWithKey.class);
        Map<String, Set<String>> actualMapSet = dummy.getInjectedSet();
        assertThat(actualMapSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(actualMapSet.get("test:zset"));
        assertThat(actualList, is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_ordered_set_without_key_is_injected_into_list_of_set() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetInListWithoutKey dummy = this.injector.getInstance(OrderedSetInListWithoutKey.class);
        List<Set<String>> actualListSet = dummy.getInjectedSet();
        assertThat(actualListSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(actualListSet.get(0));
        assertThat(actualList, is(equalTo(expectedList)));
    }
    
    @Test
    public void test_that_ordered_set_without_key_is_injected_into_set_of_set() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetInSetWithoutKey dummy = this.injector.getInstance(OrderedSetInSetWithoutKey.class);
        Set<Set<String>> actualSetSet = dummy.getInjectedSet();
        assertThat(actualSetSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(Iterables.getOnlyElement(actualSetSet));
        assertThat(actualList, is(equalTo(expectedList)));
    }
}

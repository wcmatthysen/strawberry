/**
 * Strawberry Library
 * Copyright (C) 2011 - 2012
 *
 * This library is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, see <http://www.gnu.org/licenses/>.
 */
package com.github.strawberry.guice.config;

import com.github.strawberry.guice.Config;
import com.github.strawberry.guice.ConfigModule;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
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

import static com.github.strawberry.util.JedisUtil.destroyOnShutdown;
import java.util.Properties;

/**
 *
 * @author Wiehann Matthysen
 */
public class AggregateInjectionTest extends AbstractModule {
    
    Properties properties = new Properties();
    private Injector injector;
    
    @Override
    protected void configure() {
        install(new ConfigModule(properties));
    }

    @Before
    public void setup() {
        this.injector = Guice.createInjector(this);
        properties.clear();
    }

    @After
    public void teardown() {
    }
    
    
    
    public static class StringsInListContainer {
        
        @Config("test:string:*")
        private List<String> injectedStrings;
        
        public List<String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    public static class StringsInListDefaultValueContainer {
        
        @Config("test:string:*")
        private List<String> injectedStrings = ImmutableList.of(
            "value_01", "value_02", "value_03");
        
        public List<String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    @Test
    public void test_that_strings_are_injected_into_list() {
        String testString = "test_value:%s";
        List<String> expectedList = Lists.newArrayList();
        for (int i = 0; i < 10; ++i) {
            properties.put(String.format("test:string:%s", i), String.format(testString, i));
            expectedList.add(String.format(testString, i));
        }
        StringsInListContainer dummy = this.injector.getInstance(StringsInListContainer.class);
        assertThat(Sets.newHashSet(dummy.getInjectedStrings()), is(equalTo(Sets.newHashSet(expectedList))));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_list() {
        // Test for case where no value is present in redis database.
        StringsInListDefaultValueContainer dummy = this.injector.getInstance(
            StringsInListDefaultValueContainer.class);
        List<String> defaultList = ImmutableList.of("value_01", "value_02", "value_03");
        assertThat(dummy.getInjectedStrings(), is(equalTo(defaultList)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        String testString = "test_value:%s";
        List<String> expectedList = Lists.newArrayList();
        for (int i = 0; i < 10; ++i) {
            properties.put(String.format("test:string:%s", i), String.format(testString, i));
            expectedList.add(String.format(testString, i));
        }
        dummy = this.injector.getInstance(StringsInListDefaultValueContainer.class);
        assertThat(Sets.newHashSet(dummy.getInjectedStrings()), is(equalTo(Sets.newHashSet(expectedList))));
    }
    
    
    
    public static class StringsInMapContainer {
        
        @Config("test:string:*")
        private Map<String, String> injectedStrings;
        
        public Map<String, String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    public static class StringsInMapDefaultValueContainer {
        
        @Config("test:string:*")
        private Map<String, String> injectedStrings = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        
        public Map<String, String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    @Test
    public void test_that_strings_are_injected_into_map() {
        String testString = "test_value:%s";
        Map<String, String> expectedMap = Maps.newLinkedHashMap();
        for (int i = 0; i < 10; ++i) {
            properties.put(String.format("test:string:%s", i), String.format(testString, i));
            expectedMap.put(String.format("test:string:%s", i), String.format(testString, i));
        }
        StringsInMapContainer dummy = this.injector.getInstance(StringsInMapContainer.class);
        assertThat(dummy.getInjectedStrings(), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_map() {
        // Test for case where no value is present in redis database.
        StringsInMapDefaultValueContainer dummy = this.injector.getInstance(
            StringsInMapDefaultValueContainer.class);
        Map<String, String> defaultMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        assertThat(dummy.getInjectedStrings(), is(equalTo(defaultMap)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        String testString = "test_value:%s";
        Map<String, String> expectedMap = Maps.newLinkedHashMap();
        for (int i = 0; i < 10; ++i) {
            properties.put(String.format("test:string:%s", i), String.format(testString, i));
            expectedMap.put(String.format("test:string:%s", i), String.format(testString, i));
        }
        dummy = this.injector.getInstance(StringsInMapDefaultValueContainer.class);
        assertThat(dummy.getInjectedStrings(), is(equalTo(expectedMap)));
    }
    
    
    
    public static class MapsInListContainer {
        
        @Config("test:map:*")
        private List<Map<String, String>> injectedMaps;
        
        public List<Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    public static class MapsInListDefaultValueContainer {
        
        @Config("test:map:*")
        private List<Map<String, String>> injectedMaps = (List)ImmutableList.of(
            ImmutableMap.of(
                "key_11", "value_11",
                "key_12", "value_12",
                "key_13", "value_13"
            ),
            ImmutableMap.of(
                "key_21", "value_21",
                "key_22", "value_22",
                "key_23", "value_23"
            )
        );
        
        public List<Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    @Test
    public void test_that_maps_are_injected_into_list_of_map() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:map:%s", i), testMap);
        }
        MapsInListContainer dummy = this.injector.getInstance(MapsInListContainer.class);
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
    public void test_that_missing_value_causes_default_value_to_be_set_for_list_of_map() {
        // Test for case where no value is present in redis database.
        MapsInListDefaultValueContainer dummy = this.injector.getInstance(
            MapsInListDefaultValueContainer.class);
        List<Map<String, String>> defaultList = (List)ImmutableList.of(
            ImmutableMap.of(
                "key_11", "value_11",
                "key_12", "value_12",
                "key_13", "value_13"
            ),
            ImmutableMap.of(
                "key_21", "value_21",
                "key_22", "value_22",
                "key_23", "value_23"
            )
        );
        assertThat(dummy.getInjectedMaps(), is(equalTo(defaultList)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:map:%s", i), testMap);
        }
        dummy = this.injector.getInstance(MapsInListDefaultValueContainer.class);
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
    
    
    
    public static class MapsInMapContainer {
        
        @Config("test:map:*")
        private Map<String, Map<String, String>> injectedMaps;
        
        public Map<String, Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    public static class MapsInMapDefaultValueContainer {
        
        @Config("test:map:*")
        private Map<String, Map<String, String>> injectedMaps = (Map)ImmutableMap.of(
            "test_map_01", ImmutableMap.of(
                "key_11", "value_11",
                "key_12", "value_12",
                "key_13", "value_13"
            ),
            "test_map_02", ImmutableMap.of(
                "key_21", "value_21",
                "key_22", "value_22",
                "key_23", "value_23"
            )
        );
        
        public Map<String, Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    @Test
    public void test_that_maps_are_injected_into_map_of_map() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:map:%s", i), testMap);
        }
        MapsInMapContainer dummy = this.injector.getInstance(MapsInMapContainer.class);
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
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_map_of_map() {
        // Test for case where no value is present in redis database.
        MapsInMapDefaultValueContainer dummy = this.injector.getInstance(
            MapsInMapDefaultValueContainer.class);
        Map<String, Map<String, String>> defaultMap = (Map)ImmutableMap.of(
            "test_map_01", ImmutableMap.of(
                "key_11", "value_11",
                "key_12", "value_12",
                "key_13", "value_13"
            ),
            "test_map_02", ImmutableMap.of(
                "key_21", "value_21",
                "key_22", "value_22",
                "key_23", "value_23"
            )
        );
        assertThat(dummy.getInjectedMaps(), is(equalTo(defaultMap)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:map:%s", i), testMap);
        }
        dummy = this.injector.getInstance(MapsInMapDefaultValueContainer.class);
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
    
    
    
    public static class ListsInListContainer {
        
        @Config("test:list:*")
        private List<List<String>> injectedLists;
        
        public List<List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    public static class ListsInListDefaultValueContainer {
        
        @Config("test:list:*")
        private List<List<String>> injectedLists = (List)ImmutableList.of(
            ImmutableList.of("value_11", "value_12", "value_13"),
            ImmutableList.of("value_21", "value_22", "value_23")
        );
        
        public List<List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
   
    private void rpush(Map m, String key, String val) {
        if (!m.containsKey(key)) {
            m.put(key,Lists.newArrayList());
        }
        ((List)m.get(key)).add(val);
    }

    private void sadd(Map m, String key, String val) {
        if (!m.containsKey(key)) {
            m.put(key,Sets.newHashSet());
        }
        ((Set)m.get(key)).add(val);
    }
    private void zadd(Map m, String key, int i, String val) {
        if (!m.containsKey(key)) {
            m.put(key,Sets.newLinkedHashSet());
        }
        ((Set)m.get(key)).add(val);
    }
    @Test
    public void test_that_lists_are_injected_into_list_of_list() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        ListsInListContainer dummy = this.injector.getInstance(ListsInListContainer.class);
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
    public void test_that_missing_value_causes_default_value_to_be_set_for_list_of_list() {
        // Test for case where no value is present in redis database.
        ListsInListDefaultValueContainer dummy = this.injector.getInstance(
            ListsInListDefaultValueContainer.class);
        List<List<String>> defaultList = (List)ImmutableList.of(
            ImmutableList.of("value_11", "value_12", "value_13"),
            ImmutableList.of("value_21", "value_22", "value_23")
        );
        assertThat(dummy.getInjectedLists(), is(equalTo(defaultList)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        dummy = this.injector.getInstance(ListsInListDefaultValueContainer.class);
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
    
    
    
    public static class ListsInMapContainer {
        
        @Config("test:list:*")
        private Map<String, List<String>> injectedLists;
        
        public Map<String, List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    public static class ListsInMapDefaultValueContainer {
        
        @Config("test:list:*")
        private Map<String, List<String>> injectedLists = (Map)ImmutableMap.of(
            "test_list_01", ImmutableList.of(
                "value_11", "value_12", "value_13"
            ),
            "test_list_02", ImmutableList.of(
                "value_21", "value_22", "value_23"
            )
        );
        
        public Map<String, List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    @Test
    public void test_that_lists_are_injected_into_map_of_list() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        ListsInMapContainer dummy = this.injector.getInstance(ListsInMapContainer.class);
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
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_map_of_list() {
        // Test for case where no value is present in redis database.
        ListsInMapDefaultValueContainer dummy = this.injector.getInstance(
            ListsInMapDefaultValueContainer.class);
        Map<String, List<String>> defaultMap = (Map)ImmutableMap.of(
            "test_list_01", ImmutableList.of(
                "value_11", "value_12", "value_13"
            ),
            "test_list_02", ImmutableList.of(
                "value_21", "value_22", "value_23"
            )
        );
        assertThat(dummy.getInjectedLists(), is(equalTo(defaultMap)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        dummy = this.injector.getInstance(ListsInMapDefaultValueContainer.class);
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
    
    
    
    public static class SetsInListContainer {
        
        @Config("test:set:*")
        private List<Set<String>> injectedSets;
        
        public List<Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    public static class SetsInListDefaultValueContainer {
        
        @Config("test:set:*")
        private List<Set<String>> injectedSets = (List)ImmutableList.of(
            Sets.newHashSet("value_11", "value_12", "value_13"),
            Sets.newHashSet("value_21", "value_22", "value_23")
        );
        
        public List<Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    @Test
    public void test_that_sets_are_injected_into_list_of_set() {
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        SetsInListContainer dummy = this.injector.getInstance(SetsInListContainer.class);
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
    public void test_that_missing_value_causes_default_value_to_be_set_for_list_of_set() {
        // Test for case where no value is present in redis database.
        SetsInListDefaultValueContainer dummy = this.injector.getInstance(
            SetsInListDefaultValueContainer.class);
        List<Set<String>> defaultList = (List)ImmutableList.of(
            Sets.newHashSet("value_11", "value_12", "value_13"),
            Sets.newHashSet("value_21", "value_22", "value_23")
        );
        assertThat(dummy.getInjectedSets(), is(equalTo(defaultList)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        dummy = this.injector.getInstance(SetsInListDefaultValueContainer.class);
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

    
    
    public static class SetsInMapContainer {
        
        @Config("test:set:*")
        private Map<String, Set<String>> injectedSets;
        
        public Map<String, Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    public static class SetsInMapDefaultValueContainer {
        
        @Config("test:set:*")
        private Map<String, Set<String>> injectedSets = (Map)ImmutableMap.of(
            "test_set_01", Sets.newHashSet(
                "value_11", "value_12", "value_13"
            ),
            "test_set_02", Sets.newHashSet(
                "value_21", "value_22", "value_23"
            )
        );
        
        public Map<String, Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    @Test
    public void test_that_sets_are_injected_into_map_of_set() {
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        SetsInMapContainer dummy = this.injector.getInstance(SetsInMapContainer.class);
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
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_map_of_set() {
        // Test for case where no value is present in redis database.
        SetsInMapDefaultValueContainer dummy = this.injector.getInstance(
            SetsInMapDefaultValueContainer.class);
        Map<String, Set<String>> defaultMap = (Map)ImmutableMap.of(
            "test_set_01", Sets.newHashSet(
                "value_11", "value_12", "value_13"
            ),
            "test_set_02", Sets.newHashSet(
                "value_21", "value_22", "value_23"
            )
        );
        assertThat(dummy.getInjectedSets(), is(equalTo(defaultMap)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:set:%s", i), String.format(testValue, i));
            }
        }
        dummy = this.injector.getInstance(SetsInMapDefaultValueContainer.class);
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
    
    
    
    public static class OrderedSetsInListContainer {
        
        @Config("test:zset:*")
        private List<Set<String>> injectedSets;
        
        public List<Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    public static class OrderedSetsInListDefaultValueContainer {
        
        @Config("test:zset:*")
        private List<Set<String>> injectedSets = (List)ImmutableList.of(
            Sets.newLinkedHashSet(Lists.newArrayList("value_11", "value_12", "value_13")),
            Sets.newLinkedHashSet(Lists.newArrayList("value_21", "value_22", "value_23"))
        );
        
        public List<Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    @Test
    public void test_that_ordered_sets_are_injected_into_list_of_set() {
        List<String> testList = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                zadd(properties, String.format("test:zset:%s", i), i, String.format(testValue, i));
            }
        }
        OrderedSetsInListContainer dummy = this.injector.getInstance(OrderedSetsInListContainer.class);
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
    public void test_that_missing_value_causes_default_value_to_be_set_for_list_of_zset() {
        // Test for case where no value is present in redis database.
        OrderedSetsInListDefaultValueContainer dummy = this.injector.getInstance(
            OrderedSetsInListDefaultValueContainer.class);
        List<Set<String>> defaultList = (List)ImmutableList.of(
            Sets.newLinkedHashSet(Lists.newArrayList("value_11", "value_12", "value_13")),
            Sets.newLinkedHashSet(Lists.newArrayList("value_21", "value_22", "value_23"))
        );
        assertThat(dummy.getInjectedSets(), is(equalTo(defaultList)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        List<String> testList = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                zadd(properties, String.format("test:zset:%s", i), i, String.format(testValue, i));
            }
        }
        dummy = this.injector.getInstance(OrderedSetsInListDefaultValueContainer.class);
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
    
    
    
    public static class OrderedSetsInMapContainer {
        
        @Config("test:zset:*")
        private Map<String, Set<String>> injectedSets;
        
        public Map<String, Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    public static class OrderedSetsInMapDefaultValueContainer {
        
        @Config("test:zset:*")
        private Map<String, Set<String>> injectedSets = (Map)ImmutableMap.of(
            "test_set_01", Sets.newLinkedHashSet(
                Lists.newArrayList("value_11", "value_12", "value_13")
            ),
            "test_set_02", Sets.newLinkedHashSet(
                Lists.newArrayList("value_21", "value_22", "value_23")
            )
        );
        
        public Map<String, Set<String>> getInjectedSets() {
            return this.injectedSets;
        }
    }
    
    @Test
    public void test_that_ordered_sets_are_injected_into_map_of_set() {
        List<String> testList = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                zadd(properties, String.format("test:zset:%s", i), i, String.format(testValue, i));
            }
        }
        OrderedSetsInMapContainer dummy = this.injector.getInstance(OrderedSetsInMapContainer.class);
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
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_map_of_zset() {
        // Test for case where no value is present in redis database.
        OrderedSetsInMapDefaultValueContainer dummy = this.injector.getInstance(
            OrderedSetsInMapDefaultValueContainer.class);
        Map<String, Set<String>> defaultMap = (Map)ImmutableMap.of(
            "test_set_01", Sets.newLinkedHashSet(
                Lists.newArrayList("value_11", "value_12", "value_13")
            ),
            "test_set_02", Sets.newLinkedHashSet(
                Lists.newArrayList("value_21", "value_22", "value_23")
            )
        );
        assertThat(dummy.getInjectedSets(), is(equalTo(defaultMap)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        List<String> testList = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                zadd(properties, String.format("test:zset:%s", i), i, String.format(testValue, i));
            }
        }
        dummy = this.injector.getInstance(OrderedSetsInMapDefaultValueContainer.class);
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
    
    
    
    public static class HeterogeneousInListContainer {
        
        @Config("test:heterogeneous:*")
        private List<Object> injectedObjects;
        
        public List<Object> getInjectedObjects() {
            return this.injectedObjects;
        }
    }
    
    public static class HeterogeneousInListDefaultValueContainer {
        
        @Config("test:heterogeneous:*")
        private List<Object> injectedObjects = (List)Lists.newArrayList(
            "value_01", "value_02", "value_03",
            ImmutableMap.of(
                "key_01", "value_01",
                "key_02", "value_02",
                "key_03", "value_03"
            ),
            ImmutableList.of("value_01", "value_02", "value_03"),
            Sets.newHashSet("value_01", "value_02", "value_03"),
            Sets.newLinkedHashSet(Lists.newArrayList("value_01", "value_02", "value_03"))
        );
        
        public List<Object> getInjectedObjects() {
            return this.injectedObjects;
        }
    }
    
    @Test
    public void test_that_list_is_injected_into_list_of_objects() {
        // Test list.
        List<String> testList = Lists.newArrayList("value_1", "value_2", "value_3");
        for (String testValue : testList) {
            rpush(properties, "test:heterogeneous:1", testValue);
        }
        
        // Nested list should match expected list.
        HeterogeneousInListContainer dummy = this.injector.getInstance(HeterogeneousInListContainer.class);
        List<Object> actualObjects = dummy.getInjectedObjects();
        assertThat(actualObjects.size(), is(1));
        assertThat((List<String>)actualObjects.get(0), is(equalTo(testList)));
    }
    
    @Test
    public void test_that_objects_are_injected_into_list_of_objects() {
        // Inject some strings.
        for (int i = 0; i < 3; ++i) {
            properties.put(String.format("test:heterogeneous:%s", i), String.format("test_value:%s", i));
        }
        
        // Inject some maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:heterogeneous:%s", i), testMap);
        }
        
        // Inject some lists.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 6; i < 9; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some sets.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 9; i < 12; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some ordered sets.
        List<String> testOrderedSet = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 12; i < 15; ++i) {
            for (String testValue : testOrderedSet) {
                zadd(properties, String.format("test:heterogeneous:%s", i), i, String.format(testValue, i));
            }
        }
        
        HeterogeneousInListContainer dummy = this.injector.getInstance(HeterogeneousInListContainer.class);
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
    public void test_that_missing_value_causes_default_value_to_be_set_for_list_of_objects() {
        // Test for case where no value is present in redis database.
        HeterogeneousInListDefaultValueContainer dummy = this.injector.getInstance(
            HeterogeneousInListDefaultValueContainer.class);
        List<Object> defaultList = (List)Lists.newArrayList(
            "value_01", "value_02", "value_03",
            ImmutableMap.of(
                "key_01", "value_01",
                "key_02", "value_02",
                "key_03", "value_03"
            ),
            ImmutableList.of("value_01", "value_02", "value_03"),
            Sets.newHashSet("value_01", "value_02", "value_03"),
            Sets.newLinkedHashSet(Lists.newArrayList("value_01", "value_02", "value_03"))
        );
        assertThat(dummy.getInjectedObjects(), is(equalTo(defaultList)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        // Inject some strings.
        for (int i = 0; i < 3; ++i) {
            properties.put(String.format("test:heterogeneous:%s", i), String.format("test_value:%s", i));
        }
        
        // Inject some maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:heterogeneous:%s", i), testMap);
        }
        
        // Inject some lists.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 6; i < 9; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some sets.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 9; i < 12; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some ordered sets.
        List<String> testOrderedSet = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 12; i < 15; ++i) {
            for (String testValue : testOrderedSet) {
                zadd(properties, String.format("test:heterogeneous:%s", i), i, String.format(testValue, i));
            }
        }
        
        dummy = this.injector.getInstance(HeterogeneousInListDefaultValueContainer.class);
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
    
    
    
    public static class HeterogeneousInMapContainer {
        
        @Config("test:heterogeneous:*")
        private Map<String, Object> injectedObjects;
        
        public Map<String, Object> getInjectedObjects() {
            return this.injectedObjects;
        }
    }
    
    public static class HeterogeneousInMapDefaultValueContainer {
        
        @Config("test:heterogeneous:*")
        private Map<String, Object> injectedObjects = (Map)ImmutableMap.of(
            "test_string", "test_value",
            "test_map", ImmutableMap.of(
                "key_01", "value_01",
                "key_02", "value_02",
                "key_03", "value_03"
            ),
            "test_list", ImmutableList.of("value_01", "value_02", "value_03"),
            "test_set",  Sets.newHashSet("value_01", "value_02", "value_03"),
            "test_zset", Sets.newLinkedHashSet(Lists.newArrayList("value_01", "value_02", "value_03"))
        );
        
        public Map<String, Object> getInjectedObjects() {
            return this.injectedObjects;
        }
    }
    
    @Test
    public void test_that_map_is_injected_into_map_of_objects() {
        // Test map.
        Map<String, String> testMap = ImmutableMap.of(
            "key_1", "value_1",
            "key_2", "value_2",
            "key_3", "value_3"
        );
        properties.put("test:heterogeneous:1", testMap);
        
        // Nested map should match expected map.
        HeterogeneousInMapContainer dummy = this.injector.getInstance(HeterogeneousInMapContainer.class);
        Map<String, Object> actualObjects = dummy.getInjectedObjects();
        assertThat(actualObjects.size(), is(1));
        assertThat((Map<String, String>)actualObjects.get("test:heterogeneous:1"), is(equalTo(testMap)));
    }
    
    @Test
    public void test_that_objects_are_injected_into_map_of_objects() {
        // Inject some strings.
        for (int i = 0; i < 3; ++i) {
            properties.put(String.format("test:heterogeneous:%s", i), String.format("test_value:%s", i));
        }
        
        // Inject some maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:heterogeneous:%s", i), testMap);
        }
        
        // Inject some lists.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 6; i < 9; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some sets.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 9; i < 12; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some ordered sets.
        List<String> testOrderedSet = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 12; i < 15; ++i) {
            for (String testValue : testOrderedSet) {
                zadd(properties, String.format("test:heterogeneous:%s", i), i, String.format(testValue, i));
            }
        }
        
        HeterogeneousInMapContainer dummy = this.injector.getInstance(HeterogeneousInMapContainer.class);
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
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_map_of_objects() {
        // Test for case where no value is present in redis database.
        HeterogeneousInMapDefaultValueContainer dummy = this.injector.getInstance(
            HeterogeneousInMapDefaultValueContainer.class);
        Map<String, Object> defaultMap = (Map)ImmutableMap.of(
            "test_string", "test_value",
            "test_map", ImmutableMap.of(
                "key_01", "value_01",
                "key_02", "value_02",
                "key_03", "value_03"
            ),
            "test_list", ImmutableList.of("value_01", "value_02", "value_03"),
            "test_set",  Sets.newHashSet("value_01", "value_02", "value_03"),
            "test_zset", Sets.newLinkedHashSet(Lists.newArrayList("value_01", "value_02", "value_03"))
        );
        assertThat(dummy.getInjectedObjects(), is(equalTo(defaultMap)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        // Inject some strings.
        for (int i = 0; i < 3; ++i) {
            properties.put(String.format("test:heterogeneous:%s", i), String.format("test_value:%s", i));
        }
        
        // Inject some maps.
        for (int i = 3; i < 6; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            properties.put(String.format("test:heterogeneous:%s", i), testMap);
        }
        
        // Inject some lists.
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 6; i < 9; ++i) {
            for (String testValue : testList) {
                rpush(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some sets.
        Set<String> testSet = Sets.newHashSet("value_%s1", "value_%s2", "value_%s3");
        for (int i = 9; i < 12; ++i) {
            for (String testValue : testSet) {
                sadd(properties, String.format("test:heterogeneous:%s", i), String.format(testValue, i));
            }
        }
        
        // Inject some ordered sets.
        List<String> testOrderedSet = Lists.newArrayList("value_%s3", "value_%s2", "value_%s1");
        for (int i = 12; i < 15; ++i) {
            for (String testValue : testOrderedSet) {
                zadd(properties, String.format("test:heterogeneous:%s", i), i, String.format(testValue, i));
            }
        }
        
        dummy = this.injector.getInstance(HeterogeneousInMapDefaultValueContainer.class);
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

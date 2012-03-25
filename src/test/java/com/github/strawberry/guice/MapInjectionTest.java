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
package com.github.strawberry.guice;

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

import static com.github.strawberry.util.JedisUtil.destroyOnShutdown;

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
    
    
    
    public static class MapContainer {

        @Redis(value = "test:map", allowNull = false)
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class MapAllowNullContainer {

        @Redis("test:map")
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class MapDefaultValueContainer {
        
        @Redis("test:map")
        private Map<String, String> injectedMap = ImmutableMap.of(
            "def_key_01", "def_value_01",
            "def_key_02", "def_value_02",
            "def_key_03", "def_value_03"
        );
        
        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    @Test
    public void test_that_map_is_injected_into_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapContainer dummy = this.injector.getInstance(MapContainer.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_map() {
        MapAllowNullContainer dummy = this.injector.getInstance(
            MapAllowNullContainer.class);
        assertThat(dummy.getInjectedMap(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_map_into_map() {
        MapContainer dummy = this.injector.getInstance(MapContainer.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(Collections.EMPTY_MAP)));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_map() {
        // Test for case where no value is present in redis database.
        MapDefaultValueContainer dummy = this.injector.getInstance(
            MapDefaultValueContainer.class);
        Map<String, String> defaultMap = ImmutableMap.of(
            "def_key_01", "def_value_01",
            "def_key_02", "def_value_02",
            "def_key_03", "def_value_03"
        );
        assertThat(dummy.getInjectedMap(), is(equalTo(defaultMap)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        dummy = this.injector.getInstance(MapDefaultValueContainer.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(expectedMap)));
    }
    
    
    
    public static class MapInMapContainer {

        @Redis("test:map")
        private Map<String, Map<String, String>> injectedMap;

        public Map<String, Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    @Test
    public void test_that_map_is_injected_into_map_of_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapInMapContainer dummy = this.injector.getInstance(MapInMapContainer.class);
        Map<String, Map<String, String>> actualMapMap = dummy.getInjectedMap();
        assertThat(actualMapMap.size(), is(1));
        assertThat(actualMapMap.get("test:map"), is(equalTo(expectedMap)));
    }
    
    
    
    public static class MapInListContainer {

        @Redis("test:map")
        private List<Map<String, String>> injectedMap;

        public List<Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    @Test
    public void test_that_map_is_injected_into_list_of_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapInListContainer dummy = this.injector.getInstance(MapInListContainer.class);
        List<Map<String, String>> actualMapList = dummy.getInjectedMap();
        assertThat(actualMapList.size(), is(1));
        assertThat(actualMapList.get(0), is(equalTo(expectedMap)));
    }
    
    
    
    public static class MapInSetContainer {
        
        @Redis("test:map")
        private Set<Map<String, String>> injectedMap;
        
        public Set<Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    @Test
    public void test_that_map_is_injected_into_set_of_map() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        MapInSetContainer dummy = this.injector.getInstance(MapInSetContainer.class);
        Set<Map<String, String>> actualMapSet = dummy.getInjectedMap();
        assertThat(actualMapSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualMapSet), is(equalTo(expectedMap)));
    }
}

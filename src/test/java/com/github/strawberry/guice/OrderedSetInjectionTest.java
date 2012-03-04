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

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
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
public class OrderedSetInjectionTest extends AbstractModule {

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
    
    
    
    public static class OrderedSetWithoutKey {
        
        @Redis(value = "test:zset", allowNull = false)
        private Set<String> injectedOrderedSet;

        public Set<String> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
    }
    
    public static class OrderedSetWithoutKeyAllowNull {

        @Redis("test:zset")
        private Set<String> injectedOrderedSet;

        public Set<String> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
    }
    
    public static class OrderedSetWithoutKeyDefaultValue {
        
        @Redis("test:zset")
        private Set<String> injectedOrderedSet = Sets.newLinkedHashSet(
            Lists.newArrayList("def_value_03", "def_value_02", "def_value_01")
        );
        
        public Set<String> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
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
    public void test_that_missing_value_is_injected_as_null_into_set() {
        OrderedSetWithoutKeyAllowNull dummy = this.injector.getInstance(
            OrderedSetWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedOrderedSet(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_empty_set_into_set() {
        OrderedSetWithoutKey dummy = this.injector.getInstance(OrderedSetWithoutKey.class);
        assertThat(dummy.getInjectedOrderedSet(), is(equalTo(Collections.EMPTY_SET)));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_set() {
        // Test for case where no value is present in redis database.
        OrderedSetWithoutKeyDefaultValue dummy = this.injector.getInstance(
            OrderedSetWithoutKeyDefaultValue.class);
        Set<String> defaultSet = Sets.newLinkedHashSet(
            Lists.newArrayList("def_value_03", "def_value_02", "def_value_01")
        );
        assertThat(dummy.getInjectedOrderedSet(), is(equalTo(defaultSet)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        dummy = this.injector.getInstance(OrderedSetWithoutKeyDefaultValue.class);
        List<String> actualList = Lists.newArrayList(dummy.getInjectedOrderedSet());
        assertThat(actualList, is(equalTo(expectedList)));
    }
    
    
    
    public static class OrderedSetAsListWithoutKey {
        
        @Redis("test:zset")
        private List<String> injectedList;
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
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
    
    
    
    public static class OrderedSetWithKey {

        @Redis(value = "test:zset", includeKeys = true)
        private Map<String, Set<String>> injectedOrderedSet;

        public Map<String, Set<String>> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
    }
    
    @Test
    public void test_that_ordered_set_with_key_is_injected_into_map_of_set() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetWithKey dummy = this.injector.getInstance(OrderedSetWithKey.class);
        Map<String, Set<String>> actualMapSet = dummy.getInjectedOrderedSet();
        assertThat(actualMapSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(actualMapSet.get("test:zset"));
        assertThat(actualList, is(equalTo(expectedList)));
    }
    
    
    
    public static class OrderedSetInListWithoutKey {
        
        @Redis(value = "test:zset", alwaysNest = true)
        private List<Set<String>> injectedOrderedSet;
        
        public List<Set<String>> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
    }
    
    @Test
    public void test_that_ordered_set_without_key_is_injected_into_list_of_set() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetInListWithoutKey dummy = this.injector.getInstance(OrderedSetInListWithoutKey.class);
        List<Set<String>> actualListSet = dummy.getInjectedOrderedSet();
        assertThat(actualListSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(actualListSet.get(0));
        assertThat(actualList, is(equalTo(expectedList)));
    }
    
    
    
    public static class OrderedSetInSetWithoutKey {

        @Redis(value = "test:zset", alwaysNest = true)
        private Set<Set<String>> injectedOrderedSet;

        public Set<Set<String>> getInjectedOrderedSet() {
            return this.injectedOrderedSet;
        }
    }
    
    @Test
    public void test_that_ordered_set_without_key_is_injected_into_set_of_set() {
        List<String> expectedList = Lists.newArrayList("value_03", "value_02", "value_01");
        for (int i = 0; i < expectedList.size(); ++i) {
            this.jedis.zadd("test:zset", i, expectedList.get(i));
        }
        OrderedSetInSetWithoutKey dummy = this.injector.getInstance(OrderedSetInSetWithoutKey.class);
        Set<Set<String>> actualSetSet = dummy.getInjectedOrderedSet();
        assertThat(actualSetSet.size(), is(1));
        List<String> actualList = Lists.newArrayList(Iterables.getOnlyElement(actualSetSet));
        assertThat(actualList, is(equalTo(expectedList)));
    }
}

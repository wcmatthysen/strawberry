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
public class SetInjectionTest extends AbstractModule {

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



    public static class SetWithoutKey {

        @Redis(value = "test:set", allowNull = false)
        private Set<String> injectedSet;

        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SetWithoutKeyAllowNull {

        @Redis("test:set")
        private Set<String> injectedSet;

        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
    }
    
    public static class SetWithoutKeyDefaultValue {
        
        @Redis("test:set")
        private Set<String> injectedSet = Sets.newHashSet(
            "def_value_01", "def_value_02", "def_value_03"
        );
        
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
    public void test_that_missing_value_causes_default_value_to_be_set_for_set() {
        // Test for case where no value is present in redis database.
        SetWithoutKeyDefaultValue dummy = this.injector.getInstance(
            SetWithoutKeyDefaultValue.class);
        Set<String> defaultSet = Sets.newHashSet(
            "def_value_01", "def_value_02", "def_value_03"
        );
        assertThat(dummy.getInjectedSet(), is(equalTo(defaultSet)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        Set<String> expectedSet = Sets.newHashSet("value_01", "value_02", "value_03");
        for (String value : expectedSet) {
            this.jedis.sadd("test:set", value);
        }
        dummy = this.injector.getInstance(SetWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedSet(), is(equalTo(expectedSet)));
    }
    
    
    
    public static class SetAsListWithoutKey {

        @Redis("test:set")
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
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
    
    
    
    public static class SetWithKey {

        @Redis(value = "test:set", includeKeys = true)
        private Map<String, Set<String>> injectedSet;

        public Map<String, Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
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
    
    
    
    public static class SetInListWithoutKey {

        @Redis(value = "test:set", alwaysNest = true)
        private List<Set<String>> injectedSet;

        public List<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
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
    
    
    
    public static class SetInSetWithoutKey {

        @Redis(value = "test:set", alwaysNest = true)
        private Set<Set<String>> injectedSet;

        public Set<Set<String>> getInjectedSet() {
            return this.injectedSet;
        }
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
}

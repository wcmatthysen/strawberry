/**
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
package org.strawberry.guice;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
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
import static org.strawberry.util.JedisUtil.destroyOnShutdown;

/**
 *
 * @author Wiehann Matthysen
 */
public class ListInjectionTest extends AbstractModule {
    
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
    
    
    
    public static class ListWithoutKey {

        @Redis(value = "test:list", allowNull = false)
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class ListWithoutKeyAllowNull {

        @Redis(value = "test:list")
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class ListWithoutKeyDefaultValue {
        
        @Redis(value = "test:list")
        private List<String> injectedList = ImmutableList.of(
            "def_value_01", "def_value_02", "def_value_03"
        );
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
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
    public void test_that_missing_value_causes_default_value_to_be_set_for_list() {
        // Test for case where no value is present in redis database.
        ListWithoutKeyDefaultValue dummy = this.injector.getInstance(
            ListWithoutKeyDefaultValue.class);
        List<String> defaultList = ImmutableList.of("def_value_01", "def_value_02", "def_value_03");
        assertThat(dummy.getInjectedList(), is(equalTo(defaultList)));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        dummy = this.injector.getInstance(ListWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedList(), is(equalTo(expectedList)));
    }
    
    
    
    public static class ListAsSetWithoutKey {
        
        @Redis("test:list")
        private Set<String> injectedSet;
        
        public Set<String> getInjectedSet() {
            return this.injectedSet;
        }
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
    
    
    
    public static class ListWithKey {

        @Redis(value = "test:list", includeKeys = true)
        private Map<String, List<String>> injectedList;

        public Map<String, List<String>> getInjectedList() {
            return this.injectedList;
        }
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
    
    
    
    public static class ListInListWithoutKey {

        @Redis(value = "test:list", alwaysNest = true)
        private List<List<String>> injectedList;

        public List<List<String>> getInjectedList() {
            return this.injectedList;
        }
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
    
    
    
    public static class ListInSetWithoutKey {
        
        @Redis(value = "test:list", alwaysNest = true)
        private Set<List<String>> injectedList;
        
        public Set<List<String>> getInjectedList() {
            return this.injectedList;
        }
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
}

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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
public class StringInjectionTest extends AbstractModule {

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



    public static class StringContainer {

        @Redis(value = "test:string", allowNull = false)
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class StringAllowNullContainer {

        @Redis("test:string")
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class StringDefaultValueContainer {

        @Redis("test:string")
        private String injectedString = "default_value";

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    @Test
    public void test_that_string_is_injected_into_string() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringContainer dummy = this.injector.getInstance(StringContainer.class);
        assertThat(dummy.getInjectedString(), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_string() {
        StringAllowNullContainer dummy = this.injector.getInstance(
            StringAllowNullContainer.class);
        assertThat(dummy.getInjectedString(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_blank_into_string() {
        StringContainer dummy = this.injector.getInstance(StringContainer.class);
        assertThat(dummy.getInjectedString(), is(equalTo("")));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_string() {
        // Test for case where no value is present in redis database.
        StringDefaultValueContainer dummy = this.injector.getInstance(
            StringDefaultValueContainer.class);
        assertThat(dummy.getInjectedString(), is(equalTo("default_value")));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        dummy = this.injector.getInstance(StringDefaultValueContainer.class);
        assertThat(dummy.getInjectedString(), is(equalTo(expectedString)));
    }
    
    
    
    public static class StringInMapContainer {

        @Redis("test:string")
        private Map<String, String> injectedString;

        public Map<String, String> getInjectedString() {
            return this.injectedString;
        }
    }

    @Test
    public void test_that_string_is_injected_into_map() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringInMapContainer dummy = this.injector.getInstance(StringInMapContainer.class);
        Map<String, String> actualStringMap = dummy.getInjectedString();
        assertThat(actualStringMap.size(), is(1));
        assertThat(actualStringMap.get("test:string"), is(equalTo(expectedString)));
    }
    
    
    
    public static class StringInListContainer {

        @Redis("test:string")
        private List<String> injectedString;

        public List<String> getInjectedString() {
            return this.injectedString;
        }
    }

    @Test
    public void test_that_string_is_injected_into_list() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringInListContainer dummy = this.injector.getInstance(StringInListContainer.class);
        List<String> actualStringList = dummy.getInjectedString();
        assertThat(actualStringList.size(), is(1));
        assertThat(actualStringList.get(0), is(equalTo(expectedString)));
    }
    
    
    
    public static class StringInSetContainer {

        @Redis("test:string")
        private Set<String> injectedString;

        public Set<String> getInjectedString() {
            return this.injectedString;
        }
    }

    @Test
    public void test_that_string_is_injected_into_set() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringInSetContainer dummy = this.injector.getInstance(StringInSetContainer.class);
        Set<String> actualStringSet = dummy.getInjectedString();
        assertThat(actualStringSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualStringSet), is(equalTo(expectedString)));
    }
}

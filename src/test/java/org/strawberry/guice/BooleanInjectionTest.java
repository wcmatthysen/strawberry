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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;
import static org.strawberry.util.JedisUtil.destroyOnShutdown;

/**
 *
 * @author Wiehann Matthysen
 */
public class BooleanInjectionTest extends AbstractModule {

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



    public static class PrimitiveBooleanWithoutKey {

        @Redis(value = "test:boolean", allowNull = false)
        private boolean injectedBoolean;

        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }

    public static class PrimitiveBooleanWithoutKeyAllowNull {

        @Redis(value = "test:boolean", forceUpdate = true)
        private boolean injectedBoolean;

        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class PrimitiveBooleanWithoutKeyDefaultValue {
        
        @Redis("test:boolean")
        private boolean injectedBoolean = true;
        
        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }

    @Test
    public void test_that_string_without_key_is_converted_into_primitive_boolean() {
        this.jedis.set("test:boolean", "true");
        PrimitiveBooleanWithoutKey dummy = this.injector.getInstance(
            PrimitiveBooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "F");
        dummy = this.injector.getInstance(PrimitiveBooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
        this.jedis.set("test:boolean", "Yes");
        dummy = this.injector.getInstance(PrimitiveBooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "0");
        dummy = this.injector.getInstance(PrimitiveBooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_boolean_to_null() {
        this.injector.getInstance(PrimitiveBooleanWithoutKeyAllowNull.class);
    }

    @Test
    public void test_that_missing_value_is_injected_as_false_into_primitive_boolean() {
        PrimitiveBooleanWithoutKey dummy = this.injector.getInstance(
            PrimitiveBooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_primitive_boolean() {
        // Test for case where no value is present in redis database.
        PrimitiveBooleanWithoutKeyDefaultValue dummy = this.injector.getInstance(
                PrimitiveBooleanWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:boolean", "false");
        dummy = this.injector.getInstance(PrimitiveBooleanWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_boolean() {
        this.jedis.set("test:boolean", "waar");
        this.injector.getInstance(PrimitiveBooleanWithoutKey.class);
    }



    public static class BooleanWithoutKey {

        @Redis(value = "test:boolean", allowNull = false)
        private Boolean injectedBoolean;

        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }

    public static class BooleanWithoutKeyAllowNull {

        @Redis("test:boolean")
        private Boolean injectedBoolean;

        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class BooleanWithoutKeyDefaultValue {
        
        @Redis("test:boolean")
        private Boolean injectedBoolean = Boolean.TRUE;
        
        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }

    @Test
    public void test_that_string_without_key_is_converted_into_boolean() {
        this.jedis.set("test:boolean", "y");
        BooleanWithoutKey dummy = this.injector.getInstance(BooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "No");
        dummy = this.injector.getInstance(BooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
        this.jedis.set("test:boolean", "1");
        dummy = this.injector.getInstance(BooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "FALSE");
        dummy = this.injector.getInstance(BooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }

    @Test
    public void test_that_missing_value_is_injected_as_null_into_boolean() {
        BooleanWithoutKeyAllowNull dummy = this.injector.getInstance(
            BooleanWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedBoolean(), is(nullValue()));
    }

    @Test
    public void test_that_missing_value_is_injected_as_false_into_boolean() {
        BooleanWithoutKey dummy = this.injector.getInstance(BooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }
    
    @Test
    public void test_that_missing_value_causes_default_value_to_be_set_for_boolean() {
        // Test for case where no value is present in redis database.
        BooleanWithoutKeyDefaultValue dummy = this.injector.getInstance(
                BooleanWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        
        // Test for case where value is present in redis database.
        // Default value should be overwritten.
        this.jedis.set("test:boolean", "false");
        dummy = this.injector.getInstance(BooleanWithoutKeyDefaultValue.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }

    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_boolean() {
        this.jedis.set("test:boolean", "vals");
        this.injector.getInstance(BooleanWithoutKey.class);
    }
}

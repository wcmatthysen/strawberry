package org.strawberry.guice;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.apache.commons.lang3.ArrayUtils;
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
public class CharInjectionTest extends AbstractModule {
    
    private final JedisPool pool = new JedisPool("localhost", 6379);
    
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
    
    
    
    public static class PrimitiveCharArrayWithoutKey {
        
        @Redis("test:chars")
        private char[] injectedChars;
        
        public char[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    public static class PrimitiveCharArrayWithoutKeyAllowNull {
        
        @Redis(value = "test:chars", allowNull = true)
        private char[] injectedChars;
        
        public char[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_primitive_char_array() {
        this.jedis.set("test:chars", "test_value");
        PrimitiveCharArrayWithoutKey dummy = this.injector.getInstance(
            PrimitiveCharArrayWithoutKey.class);
        assertThat(dummy.getInjectedChars(), is(equalTo("test_value".toCharArray())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_primitive_char_array() {
        PrimitiveCharArrayWithoutKeyAllowNull dummy = this.injector.getInstance(
            PrimitiveCharArrayWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedChars(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_primitive_char_array() {
        PrimitiveCharArrayWithoutKey dummy = this.injector.getInstance(
            PrimitiveCharArrayWithoutKey.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new char[]{})));
    }
    
    
    
    public static class CharArrayWithoutKey {
        
        @Redis("test:chars")
        private Character[] injectedChars;
        
        public Character[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    public static class CharArrayWithoutKeyAllowNull {
        
        @Redis(value = "test:chars", allowNull = true)
        private Character[] injectedChars;
        
        public Character[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_char_array() {
        this.jedis.set("test:chars", "test_value");
        CharArrayWithoutKey dummy = this.injector.getInstance(CharArrayWithoutKey.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedChars()), is(equalTo("test_value".toCharArray())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_char_array() {
        CharArrayWithoutKeyAllowNull dummy = this.injector.getInstance(
            CharArrayWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedChars(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_char_array() {
        CharArrayWithoutKey dummy = this.injector.getInstance(CharArrayWithoutKey.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new Character[]{})));
    }
    
    
    
    public static class PrimitiveCharWithoutKey {
        
        @Redis("test:char")
        private char injectedChar;
        
        public char getInjectedChar() {
            return this.injectedChar;
        }
    }
    
    public static class PrimitiveCharWithoutKeyAllowNull {
        
        @Redis(value = "test:char", allowNull = true)
        private char injectedChar;
        
        public char getInjectedChar() {
            return this.injectedChar;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_char() {
        this.jedis.set("test:char", "A");
        PrimitiveCharWithoutKey dummy = this.injector.getInstance(PrimitiveCharWithoutKey.class);
        assertThat(dummy.getInjectedChar(), is('A'));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_char_to_null() {
        this.injector.getInstance(PrimitiveCharWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_character_into_primitive_char() {
        PrimitiveCharWithoutKey dummy = this.injector.getInstance(PrimitiveCharWithoutKey.class);
        assertThat(dummy.getInjectedChar(), is('\0'));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_char() {
        this.jedis.set("test:char", "invalid");
        this.injector.getInstance(PrimitiveCharWithoutKey.class);
    }
    
    
    
    public static class CharWithoutKey {
        
        @Redis("test:char")
        private Character injectedChar;
        
        public Character getInjectedChar() {
            return this.injectedChar;
        }
    }
    
    public static class CharWithoutKeyAllowNull {
        
        @Redis(value = "test:char", allowNull = true)
        private Character injectedChar;
        
        public Character getInjectedChar() {
            return this.injectedChar;
        }
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_char() {
        this.jedis.set("test:char", "A");
        CharWithoutKey dummy = this.injector.getInstance(CharWithoutKey.class);
        assertThat(dummy.getInjectedChar(), is('A'));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_char() {
        CharWithoutKeyAllowNull dummy = this.injector.getInstance(
            CharWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedChar(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_char() {
        CharWithoutKey dummy = this.injector.getInstance(CharWithoutKey.class);
        assertThat(dummy.getInjectedChar(), is('\0'));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_char() {
        this.jedis.set("test:char", "invalid");
        this.injector.getInstance(CharWithoutKey.class);
    }
}

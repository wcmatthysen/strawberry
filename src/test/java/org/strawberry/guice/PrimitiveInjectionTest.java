package org.strawberry.guice;

import com.google.common.collect.Iterables;
import java.util.Set;
import com.google.inject.Guice;
import org.junit.After;
import org.junit.Before;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
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
public class PrimitiveInjectionTest extends AbstractModule {

    private final JedisPool pool = new JedisPool("localhost", 6379);
    
    private Injector injector;
    private Jedis jedis;
    
    public static class SinglePrimitiveCharArrayWithoutKeyClass {
        
        @Redis("test:chars")
        private char[] injectedChars;
        
        public char[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    public static class SinglePrimitiveCharArrayWithoutKeyAllowNullClass {
        
        @Redis(value = "test:chars", allowNull = true)
        private char[] injectedChars;
        
        public char[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    public static class SingleCharArrayWithoutKeyClass {
        
        @Redis("test:chars")
        private Character[] injectedChars;
        
        public Character[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    public static class SingleCharArrayWithoutKeyAllowNullClass {
        
        @Redis(value = "test:chars", allowNull = true)
        private Character[] injectedChars;
        
        public Character[] getInjectedChars() {
            return this.injectedChars;
        }
    }
    
    
    
    public static class SingleStringWithoutKeyClass {

        @Redis("test:string")
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
        public static class SingleStringWithoutKeyAllowNullClass {

        @Redis(value = "test:string", allowNull = true)
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    
    
    public static class SinglePrimitiveByteArrayWithoutKeyClass {

        @Redis("test:bytes")
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class SinglePrimitiveByteArrayWithoutKeyAllowNullClass {

        @Redis(value = "test:bytes", allowNull = true)
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }

    public static class SingleByteArrayWithoutKeyClass {

        @Redis("test:bytes")
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class SingleByteArrayWithoutKeyAllowNullClass {

        @Redis(value = "test:bytes", allowNull = true)
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    
    
    public static class SinglePrimitiveBooleanWithoutKeyClass {

        @Redis("test:boolean")
        private boolean injectedBoolean;

        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class SinglePrimitiveBooleanWithoutKeyAllowNullClass {

        @Redis(value = "test:boolean", allowNull = true)
        private boolean injectedBoolean;

        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }

    public static class SingleBooleanWithoutKeyClass {

        @Redis("test:boolean")
        private Boolean injectedBoolean;

        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class SingleBooleanWithoutKeyAllowNullClass {

        @Redis(value = "test:boolean", allowNull = true)
        private Boolean injectedBoolean;

        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    
    
    public static class SinglePrimitiveIntegerWithoutKeyClass {

        @Redis("test:integer")
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class SinglePrimitiveIntegerWithoutKeyAllowNullClass {

        @Redis(value = "test:integer", allowNull = true)
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    public static class SingleIntegerWithoutKeyClass {

        @Redis("test:integer")
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class SingleIntegerWithoutKeyAllowNullClass {

        @Redis(value = "test:integer", allowNull = true)
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    
    
    public static class SinglePrimitiveDoubleWithoutKeyClass {

        @Redis("test:double")
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class SinglePrimitiveDoubleWithoutKeyAllowNullClass {

        @Redis(value = "test:double", allowNull = true)
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }

    public static class SingleDoubleWithoutKeyClass {

        @Redis("test:double")
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class SingleDoubleWithoutKeyAllowNullClass {

        @Redis(value = "test:double", allowNull = true)
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    
    
    public static class SingleStringWithKeyClass {

        @Redis(value = "test:string", includeKeys = true)
        private Map<String, String> injectedString;

        public Map<String, String> getInjectedString() {
            return this.injectedString;
        }
    }

    public static class SingleStringInListWithoutKeyClass {

        @Redis("test:string")
        private List<String> injectedString;

        public List<String> getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class SingleStringInSetWithoutKeyClass {
        
        @Redis("test:string")
        private Set<String> injectedString;
        
        public Set<String> getInjectedString() {
            return this.injectedString;
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
    public void test_that_single_string_without_key_is_injected_into_primitive_char_array_field() {
        this.jedis.set("test:chars", "test_value");
        SinglePrimitiveCharArrayWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveCharArrayWithoutKeyClass.class);
        assertThat(dummy.getInjectedChars(), is(equalTo("test_value".toCharArray())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_primitive_char_array_field() {
        SinglePrimitiveCharArrayWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SinglePrimitiveCharArrayWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedChars(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_primitive_char_array_field() {
        SinglePrimitiveCharArrayWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveCharArrayWithoutKeyClass.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new char[]{})));
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_char_array_field() {
        this.jedis.set("test:chars", "test_value");
        SingleCharArrayWithoutKeyClass dummy = this.injector.getInstance(SingleCharArrayWithoutKeyClass.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedChars()), is(equalTo("test_value".toCharArray())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_char_array_field() {
        SingleCharArrayWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SingleCharArrayWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedChars(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_char_array_field() {
        SingleCharArrayWithoutKeyClass dummy = this.injector.getInstance(
            SingleCharArrayWithoutKeyClass.class);
        assertThat(dummy.getInjectedChars(), is(equalTo(new Character[]{})));
    }
    
    
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_string_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringWithoutKeyClass dummy = this.injector.getInstance(SingleStringWithoutKeyClass.class);
        assertThat(dummy.getInjectedString(), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_string_field() {
        SingleStringWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SingleStringWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedString(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_blank_into_string_field() {
        SingleStringWithoutKeyClass dummy = this.injector.getInstance(SingleStringWithoutKeyClass.class);
        assertThat(dummy.getInjectedString(), is(equalTo("")));
    }
    
    
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_primitive_byte_array_field() {
        this.jedis.set("test:bytes", "test_value");
        SinglePrimitiveByteArrayWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveByteArrayWithoutKeyClass.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo("test_value".getBytes())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_primitive_byte_array_field() {
        SinglePrimitiveByteArrayWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SinglePrimitiveByteArrayWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_primitive_byte_array_field() {
        SinglePrimitiveByteArrayWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveByteArrayWithoutKeyClass.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new byte[]{})));
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_byte_array_field() {
        this.jedis.set("test:bytes", "test_value");
        SingleByteArrayWithoutKeyClass dummy = this.injector.getInstance(SingleByteArrayWithoutKeyClass.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedBytes()), is(equalTo("test_value".getBytes())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_byte_array_field() {
        SingleByteArrayWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SingleByteArrayWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_byte_array_field() {
        SingleByteArrayWithoutKeyClass dummy = this.injector.getInstance(
            SingleByteArrayWithoutKeyClass.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new Byte[]{})));
    }
    
    
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_primitive_boolean_field() {
        this.jedis.set("test:boolean", "true");
        SinglePrimitiveBooleanWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "F");
        dummy = this.injector.getInstance(SinglePrimitiveBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_boolean_to_null() {
        this.injector.getInstance(SinglePrimitiveBooleanWithoutKeyAllowNullClass.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_false_into_primitive_boolean_field() {
        SinglePrimitiveBooleanWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_boolean() {
        this.jedis.set("test:boolean", "waar");
        this.injector.getInstance(SinglePrimitiveBooleanWithoutKeyClass.class);
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_boolean_field() {
        this.jedis.set("test:boolean", "y");
        SingleBooleanWithoutKeyClass dummy = this.injector.getInstance(SingleBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "No");
        dummy = this.injector.getInstance(SingleBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_boolean_field() {
        SingleBooleanWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SingleBooleanWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedBoolean(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_false_into_boolean_field() {
        SingleBooleanWithoutKeyClass dummy = this.injector.getInstance(SingleBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_boolean() {
        this.jedis.set("test:boolean", "vals");
        this.injector.getInstance(SingleBooleanWithoutKeyClass.class);
    }
    
    
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_primitive_integer_field() {
        this.jedis.set("test:integer", "123");
        SinglePrimitiveIntegerWithoutKeyClass dummy = this.injector.getInstance(SinglePrimitiveIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(SinglePrimitiveIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_integer_to_null() {
        this.injector.getInstance(SinglePrimitiveIntegerWithoutKeyAllowNullClass.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_integer_field() {
        SinglePrimitiveIntegerWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_integer() {
        this.jedis.set("test:integer", "invalid");
        this.injector.getInstance(SinglePrimitiveIntegerWithoutKeyClass.class);
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_integer_field() {
        this.jedis.set("test:integer", "123");
        SingleIntegerWithoutKeyClass dummy = this.injector.getInstance(SingleIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(SingleIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_integer_field() {
        SingleIntegerWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SingleIntegerWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedInteger(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_integer_field() {
        SingleIntegerWithoutKeyClass dummy = this.injector.getInstance(SingleIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_integer() {
        this.jedis.set("test:integer", "invalid");
        this.injector.getInstance(SingleIntegerWithoutKeyClass.class);
    }
    
    
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_primitive_double_field() {
        this.jedis.set("test:double", "123.456");
        SinglePrimitiveDoubleWithoutKeyClass dummy = this.injector.getInstance(SinglePrimitiveDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        this.jedis.set("test:double", ".123");
        dummy = this.injector.getInstance(SinglePrimitiveDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_double_to_null() {
        this.injector.getInstance(SinglePrimitiveDoubleWithoutKeyAllowNullClass.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_double_field() {
        SinglePrimitiveDoubleWithoutKeyClass dummy = this.injector.getInstance(
            SinglePrimitiveDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_double() {
        this.jedis.set("test:double", "invalid");
        this.injector.getInstance(SinglePrimitiveDoubleWithoutKeyClass.class);
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_double_field() {
        this.jedis.set("test:double", "123.456");
        SingleDoubleWithoutKeyClass dummy = this.injector.getInstance(SingleDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        this.jedis.set("test:double", ".123");
        dummy = this.injector.getInstance(SingleDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_double_field() {
        SingleDoubleWithoutKeyAllowNullClass dummy = this.injector.getInstance(
            SingleDoubleWithoutKeyAllowNullClass.class);
        assertThat(dummy.getInjectedDouble(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_double_field() {
        SingleDoubleWithoutKeyClass dummy = this.injector.getInstance(SingleDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_double() {
        this.jedis.set("test:double", "invalid");
        this.injector.getInstance(SingleDoubleWithoutKeyClass.class);
    }
    
    
    
    @Test
    public void test_that_single_string_with_key_is_injected_into_map_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringWithKeyClass dummy = this.injector.getInstance(SingleStringWithKeyClass.class);
        Map<String, String> actualStringMap = dummy.getInjectedString();
        assertThat(actualStringMap.size(), is(1));
        assertThat(actualStringMap.get("test:string"), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_list_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringInListWithoutKeyClass dummy = this.injector.getInstance(SingleStringInListWithoutKeyClass.class);
        List<String> actualStringList = dummy.getInjectedString();
        assertThat(actualStringList.size(), is(1));
        assertThat(actualStringList.get(0), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_set_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringInSetWithoutKeyClass dummy = this.injector.getInstance(SingleStringInSetWithoutKeyClass.class);
        Set<String> actualStringSet = dummy.getInjectedString();
        assertThat(actualStringSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualStringSet), is(equalTo(expectedString)));
    }
}

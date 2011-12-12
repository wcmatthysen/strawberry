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
    
    
    
    public static class StringWithoutKey {

        @Redis("test:string")
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
        public static class StringWithoutKeyAllowNull {

        @Redis(value = "test:string", allowNull = true)
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    
    
    public static class PrimitiveByteArrayWithoutKey {

        @Redis("test:bytes")
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class PrimitiveByteArrayWithoutKeyAllowNull {

        @Redis(value = "test:bytes", allowNull = true)
        private byte[] injectedBytes;

        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }

    public static class ByteArrayWithoutKey {

        @Redis("test:bytes")
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class ByteArrayWithoutKeyAllowNull {

        @Redis(value = "test:bytes", allowNull = true)
        private Byte[] injectedBytes;

        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    
    
    public static class PrimitiveByteWithoutKey {

        @Redis("test:byte")
        private byte injectedByte;

        public byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    public static class PrimitiveByteWithoutKeyAllowNull {

        @Redis(value = "test:byte", allowNull = true)
        private byte injectedByte;

        public byte getInjectedByte() {
            return this.injectedByte;
        }
    }

    public static class ByteWithoutKey {

        @Redis("test:byte")
        private Byte injectedByte;

        public Byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    public static class ByteWithoutKeyAllowNull {

        @Redis(value = "test:byte", allowNull = true)
        private Byte injectedByte;

        public Byte getInjectedByte() {
            return this.injectedByte;
        }
    }
    
    
    
    public static class PrimitiveBooleanWithoutKey {

        @Redis("test:boolean")
        private boolean injectedBoolean;

        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class PrimitiveBooleanWithoutKeyAllowNull {

        @Redis(value = "test:boolean", allowNull = true)
        private boolean injectedBoolean;

        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }

    public static class BooleanWithoutKey {

        @Redis("test:boolean")
        private Boolean injectedBoolean;

        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class BooleanWithoutKeyAllowNull {

        @Redis(value = "test:boolean", allowNull = true)
        private Boolean injectedBoolean;

        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    
    
    public static class PrimitiveShortWithoutKey {

        @Redis("test:short")
        private short injectedShort;

        public short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    public static class PrimitiveShortWithoutKeyAllowNull {

        @Redis(value = "test:short", allowNull = true)
        private short injectedShort;

        public short getInjectedShort() {
            return this.injectedShort;
        }
    }

    public static class ShortWithoutKey {

        @Redis("test:short")
        private Short injectedShort;

        public Short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    public static class ShortWithoutKeyAllowNull {

        @Redis(value = "test:short", allowNull = true)
        private Short injectedShort;

        public Short getInjectedShort() {
            return this.injectedShort;
        }
    }
    
    
    
    public static class PrimitiveIntegerWithoutKey {

        @Redis("test:integer")
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class PrimitiveIntegerWithoutKeyAllowNull {

        @Redis(value = "test:integer", allowNull = true)
        private int injectedInteger;

        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }

    public static class IntegerWithoutKey {

        @Redis("test:integer")
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class IntegerWithoutKeyAllowNull {

        @Redis(value = "test:integer", allowNull = true)
        private Integer injectedInteger;

        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    
    
    public static class PrimitiveLongWithoutKey {

        @Redis("test:long")
        private long injectedLong;

        public long getInjectedLong() {
            return this.injectedLong;
        }
    }
    
    public static class PrimitiveLongWithoutKeyAllowNull {

        @Redis(value = "test:long", allowNull = true)
        private long injectedLong;

        public long getInjectedLong() {
            return this.injectedLong;
        }
    }

    public static class LongWithoutKey {

        @Redis("test:long")
        private Long injectedLong;

        public Long getInjectedLong() {
            return this.injectedLong;
        }
    }
    
    public static class LongWithoutKeyAllowNull {

        @Redis(value = "test:long", allowNull = true)
        private Long injectedLong;

        public Long getInjectedLong() {
            return this.injectedLong;
        }
    }
    
    
    
    public static class PrimitiveFloatWithoutKey {

        @Redis("test:float")
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class PrimitiveFloatWithoutKeyAllowNull {

        @Redis(value = "test:float", allowNull = true)
        private float injectedFloat;

        public float getInjectedFloat() {
            return this.injectedFloat;
        }
    }

    public static class FloatWithoutKey {

        @Redis("test:float")
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    public static class FloatWithoutKeyAllowNull {

        @Redis(value = "test:float", allowNull = true)
        private Float injectedFloat;

        public Float getInjectedFloat() {
            return this.injectedFloat;
        }
    }
    
    
    
    public static class PrimitiveDoubleWithoutKey {

        @Redis("test:double")
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class PrimitiveDoubleWithoutKeyAllowNull {

        @Redis(value = "test:double", allowNull = true)
        private double injectedDouble;

        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }

    public static class DoubleWithoutKey {

        @Redis("test:double")
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class DoubleWithoutKeyAllowNull {

        @Redis(value = "test:double", allowNull = true)
        private Double injectedDouble;

        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    
    
    public static class StringWithKey {

        @Redis(value = "test:string", includeKeys = true)
        private Map<String, String> injectedString;

        public Map<String, String> getInjectedString() {
            return this.injectedString;
        }
    }

    public static class StringInListWithoutKey {

        @Redis("test:string")
        private List<String> injectedString;

        public List<String> getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class StringInSetWithoutKey {
        
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
    
    
    
    @Test
    public void test_that_string_without_key_is_injected_into_string() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringWithoutKey dummy = this.injector.getInstance(StringWithoutKey.class);
        assertThat(dummy.getInjectedString(), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_string() {
        StringWithoutKeyAllowNull dummy = this.injector.getInstance(
            StringWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedString(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_blank_into_string() {
        StringWithoutKey dummy = this.injector.getInstance(StringWithoutKey.class);
        assertThat(dummy.getInjectedString(), is(equalTo("")));
    }
    
    
    
    @Test
    public void test_that_string_without_key_is_injected_into_primitive_byte_array() {
        this.jedis.set("test:bytes", "test_value");
        PrimitiveByteArrayWithoutKey dummy = this.injector.getInstance(
            PrimitiveByteArrayWithoutKey.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo("test_value".getBytes())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_primitive_byte_array() {
        PrimitiveByteArrayWithoutKeyAllowNull dummy = this.injector.getInstance(
            PrimitiveByteArrayWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_primitive_byte_array() {
        PrimitiveByteArrayWithoutKey dummy = this.injector.getInstance(
            PrimitiveByteArrayWithoutKey.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new byte[]{})));
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_byte_array() {
        this.jedis.set("test:bytes", "test_value");
        ByteArrayWithoutKey dummy = this.injector.getInstance(ByteArrayWithoutKey.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedBytes()), is(equalTo("test_value".getBytes())));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_byte_array() {
        ByteArrayWithoutKeyAllowNull dummy = this.injector.getInstance(
            ByteArrayWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedBytes(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_empty_array_into_byte_array() {
        ByteArrayWithoutKey dummy = this.injector.getInstance(
            ByteArrayWithoutKey.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo(new Byte[]{})));
    }
    
    
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_byte() {
        this.jedis.set("test:byte", "12");
        PrimitiveByteWithoutKey dummy = this.injector.getInstance(PrimitiveByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        this.jedis.set("test:byte", "-13");
        dummy = this.injector.getInstance(PrimitiveByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_byte_to_null() {
        this.injector.getInstance(PrimitiveByteWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_byte() {
        PrimitiveByteWithoutKey dummy = this.injector.getInstance(
            PrimitiveByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_byte() {
        this.jedis.set("test:byte", "invalid");
        this.injector.getInstance(PrimitiveByteWithoutKey.class);
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_byte() {
        this.jedis.set("test:byte", "12");
        ByteWithoutKey dummy = this.injector.getInstance(ByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)12));
        this.jedis.set("test:byte", "-13");
        dummy = this.injector.getInstance(ByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)-13));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_byte() {
        ByteWithoutKeyAllowNull dummy = this.injector.getInstance(
            ByteWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedByte(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_byte() {
        ByteWithoutKey dummy = this.injector.getInstance(ByteWithoutKey.class);
        assertThat(dummy.getInjectedByte(), is((byte)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_byte() {
        this.jedis.set("test:byte", "invalid");
        this.injector.getInstance(ByteWithoutKey.class);
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
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_boolean() {
        this.jedis.set("test:boolean", "waar");
        this.injector.getInstance(PrimitiveBooleanWithoutKey.class);
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_boolean() {
        this.jedis.set("test:boolean", "y");
        BooleanWithoutKey dummy = this.injector.getInstance(BooleanWithoutKey.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "No");
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
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_boolean() {
        this.jedis.set("test:boolean", "vals");
        this.injector.getInstance(BooleanWithoutKey.class);
    }
    
    
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_short() {
        this.jedis.set("test:short", "123");
        PrimitiveShortWithoutKey dummy = this.injector.getInstance(PrimitiveShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        this.jedis.set("test:short", "-123");
        dummy = this.injector.getInstance(PrimitiveShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)-123));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_short_to_null() {
        this.injector.getInstance(PrimitiveShortWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_short() {
        PrimitiveShortWithoutKey dummy = this.injector.getInstance(
            PrimitiveShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_short() {
        this.jedis.set("test:short", "invalid");
        this.injector.getInstance(PrimitiveShortWithoutKey.class);
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_short() {
        this.jedis.set("test:short", "123");
        ShortWithoutKey dummy = this.injector.getInstance(ShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)123));
        this.jedis.set("test:short", "-123");
        dummy = this.injector.getInstance(ShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)-123));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_short() {
        ShortWithoutKeyAllowNull dummy = this.injector.getInstance(
            ShortWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedShort(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_short() {
        ShortWithoutKey dummy = this.injector.getInstance(ShortWithoutKey.class);
        assertThat(dummy.getInjectedShort(), is((short)0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_short() {
        this.jedis.set("test:short", "invalid");
        this.injector.getInstance(ShortWithoutKey.class);
    }
    
    
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_integer() {
        this.jedis.set("test:integer", "123");
        PrimitiveIntegerWithoutKey dummy = this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_integer_to_null() {
        this.injector.getInstance(PrimitiveIntegerWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_integer() {
        PrimitiveIntegerWithoutKey dummy = this.injector.getInstance(
            PrimitiveIntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_integer() {
        this.jedis.set("test:integer", "invalid");
        this.injector.getInstance(PrimitiveIntegerWithoutKey.class);
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_integer() {
        this.jedis.set("test:integer", "123");
        IntegerWithoutKey dummy = this.injector.getInstance(IntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(IntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_integer() {
        IntegerWithoutKeyAllowNull dummy = this.injector.getInstance(
            IntegerWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedInteger(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_integer() {
        IntegerWithoutKey dummy = this.injector.getInstance(IntegerWithoutKey.class);
        assertThat(dummy.getInjectedInteger(), is(0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_integer() {
        this.jedis.set("test:integer", "invalid");
        this.injector.getInstance(IntegerWithoutKey.class);
    }
    
    
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_long() {
        this.jedis.set("test:long", "123");
        PrimitiveLongWithoutKey dummy = this.injector.getInstance(PrimitiveLongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(123L));
        this.jedis.set("test:long", "-123");
        dummy = this.injector.getInstance(PrimitiveLongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(-123L));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_long_to_null() {
        this.injector.getInstance(PrimitiveLongWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_long() {
        PrimitiveLongWithoutKey dummy = this.injector.getInstance(
            PrimitiveLongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(0L));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_long() {
        this.jedis.set("test:long", "invalid");
        this.injector.getInstance(PrimitiveLongWithoutKey.class);
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_long() {
        this.jedis.set("test:long", "123");
        LongWithoutKey dummy = this.injector.getInstance(LongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(123L));
        this.jedis.set("test:long", "-123");
        dummy = this.injector.getInstance(LongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(-123L));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_long() {
        LongWithoutKeyAllowNull dummy = this.injector.getInstance(
            LongWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedLong(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_long() {
        LongWithoutKey dummy = this.injector.getInstance(LongWithoutKey.class);
        assertThat(dummy.getInjectedLong(), is(0L));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_long() {
        this.jedis.set("test:long", "invalid");
        this.injector.getInstance(LongWithoutKey.class);
    }
    
    
    
   @Test
    public void test_that_string_without_key_is_converted_into_primitive_float() {
        this.jedis.set("test:float", "123.456");
        PrimitiveFloatWithoutKey dummy = this.injector.getInstance(PrimitiveFloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(123.456f));
        this.jedis.set("test:float", ".123");
        dummy = this.injector.getInstance(PrimitiveFloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.123f));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_float_to_null() {
        this.injector.getInstance(PrimitiveFloatWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_float() {
        PrimitiveFloatWithoutKey dummy = this.injector.getInstance(
            PrimitiveFloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.0f));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_float() {
        this.jedis.set("test:float", "invalid");
        this.injector.getInstance(PrimitiveFloatWithoutKey.class);
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_float() {
        this.jedis.set("test:float", "123.456");
        FloatWithoutKey dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(123.456f));
        this.jedis.set("test:float", ".123");
        dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.123f));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_float() {
        FloatWithoutKeyAllowNull dummy = this.injector.getInstance(
            FloatWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedFloat(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_float() {
        FloatWithoutKey dummy = this.injector.getInstance(FloatWithoutKey.class);
        assertThat(dummy.getInjectedFloat(), is(0.0f));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_float() {
        this.jedis.set("test:float", "invalid");
        this.injector.getInstance(FloatWithoutKey.class);
    }
    
    
    
    @Test
    public void test_that_string_without_key_is_converted_into_primitive_double() {
        this.jedis.set("test:double", "123.456");
        PrimitiveDoubleWithoutKey dummy = this.injector.getInstance(PrimitiveDoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        this.jedis.set("test:double", ".123");
        dummy = this.injector.getInstance(PrimitiveDoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_missing_value_causes_exception_when_setting_primitive_double_to_null() {
        this.injector.getInstance(PrimitiveDoubleWithoutKeyAllowNull.class);
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_primitive_double() {
        PrimitiveDoubleWithoutKey dummy = this.injector.getInstance(
            PrimitiveDoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_primitive_double() {
        this.jedis.set("test:double", "invalid");
        this.injector.getInstance(PrimitiveDoubleWithoutKey.class);
    }
    
    @Test
    public void test_that_string_without_key_is_converted_into_double() {
        this.jedis.set("test:double", "123.456");
        DoubleWithoutKey dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        this.jedis.set("test:double", ".123");
        dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_null_into_double() {
        DoubleWithoutKeyAllowNull dummy = this.injector.getInstance(
            DoubleWithoutKeyAllowNull.class);
        assertThat(dummy.getInjectedDouble(), is(nullValue()));
    }
    
    @Test
    public void test_that_missing_value_is_injected_as_zero_into_double() {
        DoubleWithoutKey dummy = this.injector.getInstance(DoubleWithoutKey.class);
        assertThat(dummy.getInjectedDouble(), is(0.0));
    }
    
    @Test(expected = RuntimeException.class)
    public void test_that_invalid_string_throws_exception_when_converting_to_double() {
        this.jedis.set("test:double", "invalid");
        this.injector.getInstance(DoubleWithoutKey.class);
    }
    
    
    
    @Test
    public void test_that_string_with_key_is_injected_into_map() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringWithKey dummy = this.injector.getInstance(StringWithKey.class);
        Map<String, String> actualStringMap = dummy.getInjectedString();
        assertThat(actualStringMap.size(), is(1));
        assertThat(actualStringMap.get("test:string"), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_list() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringInListWithoutKey dummy = this.injector.getInstance(StringInListWithoutKey.class);
        List<String> actualStringList = dummy.getInjectedString();
        assertThat(actualStringList.size(), is(1));
        assertThat(actualStringList.get(0), is(equalTo(expectedString)));
    }
    
    @Test
    public void test_that_string_without_key_is_injected_into_set() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        StringInSetWithoutKey dummy = this.injector.getInstance(StringInSetWithoutKey.class);
        Set<String> actualStringSet = dummy.getInjectedString();
        assertThat(actualStringSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualStringSet), is(equalTo(expectedString)));
    }
}

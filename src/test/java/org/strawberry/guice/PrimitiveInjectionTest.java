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

    public static class SingleStringWithoutKeyClass {

        @Redis("test:string")
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

    public static class SingleByteArrayWithoutKeyClass {

        @Redis("test:bytes")
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

    public static class SingleBooleanWithoutKeyClass {

        @Redis("test:boolean")
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

    public static class SingleIntegerWithoutKeyClass {

        @Redis("test:integer")
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

    public static class SingleDoubleWithoutKeyClass {

        @Redis("test:double")
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

    public static class SingleStringWithoutKeyMissingAllowNullClass {

        @Redis(value = "test:non_existent_string", allowNull = true)
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }

    public static class SingleStringWithoutKeyMissingClass {

        @Redis(value = "test:non_existent_string")
        private String injectedString;

        public String getInjectedString() {
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
        this.pool.returnResource(this.jedis);
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_string_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringWithoutKeyClass dummy = this.injector.getInstance(SingleStringWithoutKeyClass.class);
        assertThat(dummy.getInjectedString(), is(equalTo(expectedString)));
        this.jedis.del("test:string");
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_primitive_byte_array_field() {
        this.jedis.set("test:bytes", "test_value");
        SinglePrimitiveByteArrayWithoutKeyClass dummy = this.injector.getInstance(SinglePrimitiveByteArrayWithoutKeyClass.class);
        assertThat(dummy.getInjectedBytes(), is(equalTo("test_value".getBytes())));
        this.jedis.del("test:bytes");
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_byte_array_field() {
        this.jedis.set("test:bytes", "test_value");
        SingleByteArrayWithoutKeyClass dummy = this.injector.getInstance(SingleByteArrayWithoutKeyClass.class);
        assertThat(ArrayUtils.toPrimitive(dummy.getInjectedBytes()), is(equalTo("test_value".getBytes())));
        this.jedis.del("test:bytes");
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_primitive_boolean_field() {
        this.jedis.set("test:boolean", "true");
        SinglePrimitiveBooleanWithoutKeyClass dummy = this.injector.getInstance(SinglePrimitiveBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "F");
        dummy = this.injector.getInstance(SinglePrimitiveBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
        this.jedis.del("test:boolean");
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_boolean_field() {
        this.jedis.set("test:boolean", "y");
        SingleBooleanWithoutKeyClass dummy = this.injector.getInstance(SingleBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(true));
        this.jedis.set("test:boolean", "No");
        dummy = this.injector.getInstance(SingleBooleanWithoutKeyClass.class);
        assertThat(dummy.getInjectedBoolean(), is(false));
        this.jedis.del("test:boolean");
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_primitive_integer_field() {
        this.jedis.set("test:integer", "123");
        SinglePrimitiveIntegerWithoutKeyClass dummy = this.injector.getInstance(SinglePrimitiveIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(SinglePrimitiveIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
        this.jedis.del("test:integer");
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_integer_field() {
        this.jedis.set("test:integer", "123");
        SingleIntegerWithoutKeyClass dummy = this.injector.getInstance(SingleIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(123));
        this.jedis.set("test:integer", "-123");
        dummy = this.injector.getInstance(SingleIntegerWithoutKeyClass.class);
        assertThat(dummy.getInjectedInteger(), is(-123));
        this.jedis.del("test:integer");
    }
    
    @Test
    public void test_that_single_string_without_key_is_converted_into_primitive_double_field() {
        this.jedis.set("test:double", "123.456");
        SinglePrimitiveDoubleWithoutKeyClass dummy = this.injector.getInstance(SinglePrimitiveDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(123.456));
        this.jedis.set("test:double", ".123");
        dummy = this.injector.getInstance(SinglePrimitiveDoubleWithoutKeyClass.class);
        assertThat(dummy.getInjectedDouble(), is(0.123));
        this.jedis.del("test:double");
    }
    
    @Test
    public void test_that_single_string_with_key_is_injected_into_map_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringWithKeyClass dummy = this.injector.getInstance(SingleStringWithKeyClass.class);
        Map<String, String> actualStringMap = dummy.getInjectedString();
        assertThat(actualStringMap.size(), is(1));
        assertThat(actualStringMap.get("test:string"), is(equalTo(expectedString)));
        jedis.del("test:string");
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_list_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringInListWithoutKeyClass dummy = this.injector.getInstance(SingleStringInListWithoutKeyClass.class);
        List<String> actualStringList = dummy.getInjectedString();
        assertThat(actualStringList.size(), is(1));
        assertThat(actualStringList.get(0), is(equalTo(expectedString)));
        this.jedis.del("test:string");
    }
    
    @Test
    public void test_that_single_string_without_key_is_injected_into_set_field() {
        String expectedString = "test_value";
        this.jedis.set("test:string", expectedString);
        SingleStringInSetWithoutKeyClass dummy = this.injector.getInstance(SingleStringInSetWithoutKeyClass.class);
        Set<String> actualStringSet = dummy.getInjectedString();
        assertThat(actualStringSet.size(), is(1));
        assertThat(Iterables.getOnlyElement(actualStringSet), is(equalTo(expectedString)));
        this.jedis.del("test:string");
    }
    
    @Test
    public void test_that_single_string_without_key_thats_missing_is_injected_as_null_into_string_field() {
        SingleStringWithoutKeyMissingAllowNullClass dummy = this.injector.getInstance(
            SingleStringWithoutKeyMissingAllowNullClass.class);
        assertThat(dummy.getInjectedString(), is(nullValue()));
    }
    
    @Test
    public void test_that_single_string_without_key_thats_missing_is_injected_as_blank_into_string_field() {
        SingleStringWithoutKeyMissingClass dummy = this.injector.getInstance(SingleStringWithoutKeyMissingClass.class);
        assertThat(dummy.getInjectedString(), is(equalTo("")));
    }
}

package org.strawberry.guice;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.strawberry.redis.RedisCacheLoader;
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
public class RedisModuleTest extends AbstractModule {
    
    private final JedisPool pool = new JedisPool("localhost", 6379);
    
    private Injector injector;
    private Jedis jedis;

    public static class SingleStringWithoutKeyClass {

        @Redis(pattern = "test:string")
        private String injectedString;

        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class SinglePrimitiveByteArrayWithoutKeyClass {
        
        @Redis(pattern = "test:bytes")
        private byte[] injectedBytes;
        
        public byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class SingleByteArrayWithoutKeyClass {
        
        @Redis(pattern = "test:bytes")
        private Byte[] injectedBytes;
        
        public Byte[] getInjectedBytes() {
            return this.injectedBytes;
        }
    }
    
    public static class SinglePrimitiveBooleanWithoutKeyClass {
        
        @Redis(pattern = "test:boolean")
        private boolean injectedBoolean;
        
        public boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class SingleBooleanWithoutKeyClass {
        
        @Redis(pattern = "test:boolean")
        private Boolean injectedBoolean;
        
        public Boolean getInjectedBoolean() {
            return this.injectedBoolean;
        }
    }
    
    public static class SinglePrimitiveIntegerWithoutKeyClass {
        
        @Redis(pattern = "test:integer")
        private int injectedInteger;
        
        public int getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class SingleIntegerWithoutKeyClass {
        
        @Redis(pattern = "test:integer")
        private Integer injectedInteger;
        
        public Integer getInjectedInteger() {
            return this.injectedInteger;
        }
    }
    
    public static class SinglePrimitiveDoubleWithoutKeyClass {
        
        @Redis(pattern = "test:double")
        private double injectedDouble;
        
        public double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class SingleDoubleWithoutKeyClass {
        
        @Redis(pattern = "test:double")
        private Double injectedDouble;
        
        public Double getInjectedDouble() {
            return this.injectedDouble;
        }
    }
    
    public static class SingleStringWithKeyClass {
        
        @Redis(pattern = "test:string", includeKeys = true)
        private Map<String, String> injectedString;
        
        public Map<String, String> getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class SingleStringInListWithoutKeyClass {
        
        @Redis(pattern = "test:string")
        private List<String> injectedString;
        
        public List<String> getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class SingleStringWithoutKeyMissingAllowNullClass {
        
        @Redis(pattern = "test:non_existent_string", allowNull = true)
        private String injectedString;
        
        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    public static class SingleStringWithoutKeyMissingClass {
        
        @Redis(pattern = "test:non_existent_string")
        private String injectedString;
        
        public String getInjectedString() {
            return this.injectedString;
        }
    }
    
    
    
    public static class SingleMapWithoutKeyClass {

        @Redis(pattern = "test:map")
        private Map<String, String> injectedMap;

        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class SingleMapWithKeyClass {
        
        @Redis(pattern = "test:map", includeKeys = true)
        private Map<String, Map<String, String>> injectedMap;
        
        public Map<String, Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class SingleMapInListWithoutKeyClass {
        
        @Redis(pattern = "test:map")
        private List<Map<String, String>> injectedMap;
        
        public List<Map<String, String>> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class SingleMapWithoutKeyMissingAllowNullClass {
        
        @Redis(pattern = "test:non_existent_map", allowNull = true)
        private Map<String, String> injectedMap;
        
        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    public static class SingleMapWithoutKeyMissingClass {
        
        @Redis(pattern = "test:non_existent_map")
        private Map<String, String> injectedMap;
        
        public Map<String, String> getInjectedMap() {
            return this.injectedMap;
        }
    }
    
    

    public static class SingleListWithoutKeyClass {

        @Redis(pattern = "test:list")
        private List<String> injectedList;

        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class SingleListWithKeyClass {
        
        @Redis(pattern = "test:list", includeKeys = true)
        private Map<String, List<String>> injectedList;
        
        public Map<String, List<String>> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class SingleListInListWithoutKeyClass {
        
        @Redis(pattern = "test:list", alwaysNest = true)
        private List<List<String>> injectedList;
        
        public List<List<String>> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class SingleListWithoutKeyMissingAllowNullClass {
        
        @Redis(pattern = "test:non_existent_list", allowNull = true)
        private List<String> injectedList;
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    public static class SingleListWithoutKeyMissingClass {
        
        @Redis(pattern = "test:non_existent_list")
        private List<String> injectedList;
        
        public List<String> getInjectedList() {
            return this.injectedList;
        }
    }
    
    
    
    public static class MultiStringsWithoutKeysInjectClass {
        
        @Redis(pattern = "test:string:*")
        private List<String> injectedStrings;
        
        public List<String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    public static class MultiStringsWithKeysInjectClass {
        
        @Redis(pattern = "test:string:*", includeKeys = true)
        private Map<String, String> injectedStrings;
        
        public Map<String, String> getInjectedStrings() {
            return this.injectedStrings;
        }
    }
    
    public static class MultiListsWithoutKeysInjectClass {
        
        @Redis(pattern = "test:list:*")
        private List<List<String>> injectedLists;
        
        public List<List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    public static class MultiListsWithKeysInjectClass {
        
        @Redis(pattern = "test:list:*", includeKeys = true)
        private Map<String, List<String>> injectedLists;
        
        public Map<String, List<String>> getInjectedLists() {
            return this.injectedLists;
        }
    }
    
    public static class MultiMapsWithoutKeysInjectClass {
        
        @Redis(pattern = "test:map:*")
        private List<Map<String, String>> injectedMaps;
        
        public List<Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    public static class MultiMapsWithKeysInjectClass {
        
        @Redis(pattern = "test:map:*", includeKeys = true)
        private Map<String, Map<String, String>> injectedMaps;
        
        public Map<String, Map<String, String>> getInjectedMaps() {
            return this.injectedMaps;
        }
    }
    
    
    
    @Override
    protected void configure() {
        Cache<Field, Object> configCache = CacheBuilder.newBuilder().
            maximumSize(0).build(new RedisCacheLoader(this.pool));
        install(new RedisModule(configCache));
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
    
    

    @Test
    public void test_that_single_map_without_key_is_injected_into_map_field() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        SingleMapWithoutKeyClass dummy = this.injector.getInstance(SingleMapWithoutKeyClass.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(expectedMap)));
        this.jedis.del("test:map");
    }
    
    @Test
    public void test_that_single_map_with_key_is_injected_into_map_field() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        SingleMapWithKeyClass dummy = this.injector.getInstance(SingleMapWithKeyClass.class);
        Map<String, Map<String, String>> actualMapMap = dummy.getInjectedMap();
        assertThat(actualMapMap.size(), is(1));
        assertThat(actualMapMap.get("test:map"), is(equalTo(expectedMap)));
    }
    
    @Test
    public void test_that_single_map_without_key_is_injected_into_list_field() {
        Map<String, String> expectedMap = ImmutableMap.of(
            "key_01", "value_01",
            "key_02", "value_02",
            "key_03", "value_03"
        );
        this.jedis.hmset("test:map", expectedMap);
        SingleMapInListWithoutKeyClass dummy = this.injector.getInstance(SingleMapInListWithoutKeyClass.class);
        List<Map<String, String>> actualMapList = dummy.getInjectedMap();
        assertThat(actualMapList.size(), is(1));
        assertThat(actualMapList.get(0), is(equalTo(expectedMap)));
        this.jedis.del("test:map");
    }
    
    @Test
    public void test_that_single_map_without_key_thats_missing_is_injected_as_null_into_map_field() {
        SingleMapWithoutKeyMissingAllowNullClass dummy = this.injector.getInstance(
            SingleMapWithoutKeyMissingAllowNullClass.class);
        assertThat(dummy.getInjectedMap(), is(nullValue()));
    }
    
    @Test
    public void test_that_single_map_without_key_thats_missing_is_injected_as_empty_map_into_map_field() {
        SingleMapWithoutKeyMissingClass dummy = this.injector.getInstance(SingleMapWithoutKeyMissingClass.class);
        assertThat(dummy.getInjectedMap(), is(equalTo(Collections.EMPTY_MAP)));
    }
    

    
    @Test
    public void test_that_single_list_without_key_is_injected_into_list_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListWithoutKeyClass dummy = this.injector.getInstance(SingleListWithoutKeyClass.class);
        assertThat(dummy.getInjectedList(), is(equalTo(expectedList)));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_with_key_is_injected_into_map_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListWithKeyClass dummy = this.injector.getInstance(SingleListWithKeyClass.class);
        Map<String, List<String>> actualMapList = dummy.getInjectedList();
        assertThat(actualMapList.size(), is(1));
        assertThat(actualMapList.get("test:list"), is(equalTo(expectedList)));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_without_key_is_injected_into_list_of_list_field() {
        List<String> expectedList = Lists.newArrayList("value_01", "value_02", "value_03");
        for (String value : expectedList) {
            this.jedis.rpush("test:list", value);
        }
        SingleListInListWithoutKeyClass dummy = this.injector.getInstance(SingleListInListWithoutKeyClass.class);
        List<List<String>> actualListList = dummy.getInjectedList();
        assertThat(actualListList.size(), is(1));
        assertThat(actualListList.get(0), is(equalTo(expectedList)));
        this.jedis.del("test:list");
    }
    
    @Test
    public void test_that_single_list_without_key_thats_missing_is_injected_as_null_into_list_field() {
        SingleListWithoutKeyMissingAllowNullClass dummy = this.injector.getInstance(
            SingleListWithoutKeyMissingAllowNullClass.class);
        assertThat(dummy.getInjectedList(), is(nullValue()));
    }
    
    @Test
    public void test_that_single_list_without_key_thats_missing_is_injected_as_empty_list_into_list_field() {
        SingleListWithoutKeyMissingClass dummy = this.injector.getInstance(SingleListWithoutKeyMissingClass.class);
        assertThat(dummy.getInjectedList(), is(equalTo(Collections.EMPTY_LIST)));
    }
    
    
    
    @Test
    public void test_that_multiple_strings_without_keys_are_injected_into_list_field() {
        String testString = "test_value:%s";
        List<String> expectedList = Lists.newArrayList();
        for (int i = 0; i < 10; ++i) {
            this.jedis.set(String.format("test:string:%s", i), String.format(testString, i));
            expectedList.add(String.format(testString, i));
        }
        MultiStringsWithoutKeysInjectClass dummy = this.injector.getInstance(MultiStringsWithoutKeysInjectClass.class);
        assertThat(Sets.newHashSet(dummy.getInjectedStrings()), is(equalTo(Sets.newHashSet(expectedList))));
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:string:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_strings_with_keys_are_injected_into_map_field() {
        String testString = "test_value:%s";
        Map<String, String> expectedMap = Maps.newLinkedHashMap();
        for (int i = 0; i < 10; ++i) {
            this.jedis.set(String.format("test:string:%s", i), String.format(testString, i));
            expectedMap.put(String.format("test:string:%s", i), String.format(testString, i));
        }
        MultiStringsWithKeysInjectClass dummy = this.injector.getInstance(MultiStringsWithKeysInjectClass.class);
        assertThat(dummy.getInjectedStrings(), is(equalTo(expectedMap)));
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:string:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_lists_without_keys_are_injected_into_list_field() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        MultiListsWithoutKeysInjectClass dummy = this.injector.getInstance(MultiListsWithoutKeysInjectClass.class);
        List<List<String>> actualLists = dummy.getInjectedLists();
        assertThat(actualLists.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            List<String> expectedList = Lists.newArrayList(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i)
            );
            assertThat(actualLists.contains(expectedList), is(true));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:list:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_lists_with_keys_are_injected_into_map_field() {
        List<String> testList = Lists.newArrayList("value_%s1", "value_%s2", "value_%s3");
        for (int i = 0; i < 10; ++i) {
            for (String testValue : testList) {
                this.jedis.rpush(String.format("test:list:%s", i), String.format(testValue, i));
            }
        }
        MultiListsWithKeysInjectClass dummy = this.injector.getInstance(MultiListsWithKeysInjectClass.class);
        Map<String, List<String>> actualLists = dummy.getInjectedLists();
        assertThat(actualLists.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            List<String> expectedList = Lists.newArrayList(
                String.format("value_%s1", i),
                String.format("value_%s2", i),
                String.format("value_%s3", i)
            );
            assertThat(actualLists.get(String.format("test:list:%s", i)), is(equalTo(expectedList)));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:list:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_maps_without_keys_are_injected_into_list_field() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:map:%s", i), testMap);
        }
        MultiMapsWithoutKeysInjectClass dummy = this.injector.getInstance(MultiMapsWithoutKeysInjectClass.class);
        List<Map<String, String>> actualMaps = dummy.getInjectedMaps();
        assertThat(actualMaps.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Map<String, String> expectedMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            assertThat(actualMaps.contains(expectedMap), is(true));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:map:%s", i));
        }
    }
    
    @Test
    public void test_that_multiple_maps_with_keys_are_injected_into_map_field() {
        for (int i = 0; i < 10; ++i) {
            Map<String, String> testMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            this.jedis.hmset(String.format("test:map:%s", i), testMap);
        }
        MultiMapsWithKeysInjectClass dummy = this.injector.getInstance(MultiMapsWithKeysInjectClass.class);
        Map<String, Map<String, String>> actualMaps = dummy.getInjectedMaps();
        assertThat(actualMaps.size(), is(10));
        for (int i = 0; i < 10; ++i) {
            Map<String, String> expectedMap = ImmutableMap.of(
                String.format("key_%s1", i), String.format("value_%s1", i),
                String.format("key_%s2", i), String.format("value_%s2", i),
                String.format("key_%s3", i), String.format("value_%s3", i)
            );
            assertThat(actualMaps.get(String.format("test:map:%s", i)), is(equalTo(expectedMap)));
        }
        for (int i = 0; i < 10; ++i) {
            this.jedis.del(String.format("test:map:%s", i));
        }
    }
    
}

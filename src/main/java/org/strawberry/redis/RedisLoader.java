package org.strawberry.redis;

import java.util.Collection;
import com.google.common.collect.Iterables;
import org.strawberry.util.Patterns;
import org.apache.commons.lang3.ArrayUtils;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import fj.F;
import fj.data.Option;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.strawberry.guice.Redis;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import static org.strawberry.util.JedisUtil.using;

/**
 *
 * @author Wiehann Matthysen
 */
public final class RedisLoader extends CacheLoader<Field, Option> {
    
    private enum JedisType {
        STRING, HASH, LIST, SET, ZSET
    }
    
    private final JedisPool pool;

    public RedisLoader(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public Option load(Field field) throws Exception {
        return loadFromRedis(this.pool, field.getType(), field.getAnnotation(Redis.class));
    }
    
    private static Object nonNullValueOf(Class<?> type) {
        Object value = null;
        if (type.equals(char[].class)) {
            value = new char[]{};
        } else if (type.equals(Character[].class)) {
            value = new Character[]{};
        } else if (type.equals(char.class) || type.equals(Character.class)) {
            value = '\0';
        } else if (type.equals(String.class)) {
            value = "";
        } else if (type.equals(byte[].class)) {
            value = new byte[]{};
        } else if (type.equals(Byte[].class)) {
            value = new Byte[]{};
        } else if (type.equals(byte.class) || type.equals(Byte.class)) {
            value = (byte)0;
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            value = false;
        } else if (type.equals(short.class) || type.equals(Short.class)) {
            value = (short)0;
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            value = 0;
        } else if (type.equals(long.class) || type.equals(Long.class)) {
            value = 0L;
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            value = 0.0;
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            value = 0.0f;
        } else if (type.equals(Map.class)) {
            value = Maps.newHashMap();
        } else if (type.equals(List.class)) {
            value = Lists.newArrayList();
        } else if (type.equals(Set.class)) {
            value = Sets.newHashSet();
        }
        return value;
    }
    
    private static Map<String, ?> nestedMapOf(Jedis jedis, Set<String> redisKeys) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        for (String redisKey : redisKeys) {
            JedisType jedisType = JedisType.valueOf(jedis.type(redisKey).toUpperCase());
            switch (jedisType) {
                case STRING: {
                    map.put(redisKey, jedis.get(redisKey));
                } break;
                case HASH: {
                    map.put(redisKey, jedis.hgetAll(redisKey));
                } break;
                case LIST: {
                    map.put(redisKey, jedis.lrange(redisKey, 0, -1));
                } break;
                case SET: {
                    map.put(redisKey, jedis.smembers(redisKey));
                } break;
                case ZSET : {
                    map.put(redisKey, jedis.zrange(redisKey, 0, -1));
                } break;
            }
        }
        return map;
    }
    
    private static List<?> nestedListOf(Jedis jedis, Set<String> redisKeys) {
        List<Object> list = Lists.newArrayList();
        for (String redisKey : redisKeys) {
            JedisType jedisType = JedisType.valueOf(jedis.type(redisKey).toUpperCase());
            switch (jedisType) {
                case STRING: {
                    list.add(jedis.get(redisKey));
                } break;
                case HASH: {
                    list.add(jedis.hgetAll(redisKey));
                } break;
                case LIST: {
                    list.add(jedis.lrange(redisKey, 0, -1));
                } break;
                case SET: {
                    list.add(jedis.smembers(redisKey));
                } break;
                case ZSET: {
                    list.add(jedis.zrange(redisKey, 0, -1));
                } break;
            }
        }
        return list;
    }
    
    private static Collection<?> collectionOf(Class<?> fieldType, Jedis jedis, String key, boolean alwaysNest) {
        Collection<Object> collection = null;
        if (fieldType.equals(List.class)) {
            collection = Lists.newArrayList();
        } else if (fieldType.equals(Set.class)) {
            collection = Sets.newLinkedHashSet();
        }
        JedisType jedisType = JedisType.valueOf(jedis.type(key).toUpperCase());
        switch (jedisType) {
            case STRING: {
                collection.add(jedis.get(key));
            } break;
            case HASH: {
                collection.add(jedis.hgetAll(key));
            } break;
            case LIST: {
                if (alwaysNest) {
                    collection.add(jedis.lrange(key, 0, -1));
                } else {
                    collection.addAll(jedis.lrange(key, 0, -1));
                }
            } break;
            case SET: {
                if (alwaysNest) {
                    collection.add(jedis.smembers(key));
                } else {
                    collection.addAll(jedis.smembers(key));
                }
            } break;
            case ZSET: {
                if (alwaysNest) {
                    collection.add(jedis.zrange(key, 0, -1));
                } else {
                    collection.addAll(jedis.zrange(key, 0, -1));
                }
            } break;
        }
        return collection;
    }
    
    private static Option loadFromRedis(JedisPool pool, final Class<?> fieldType, final Redis annotation) {
        return using(pool)._do(new F<Jedis, Option>() {

            @Override
            public Option f(Jedis jedis) {
                Object value = null;

                String pattern = annotation.value();
                boolean includeKeys = annotation.includeKeys();
                boolean alwaysNest = annotation.alwaysNest();
                boolean allowNull = annotation.allowNull();

                Set<String> redisKeys = jedis.keys(pattern);
                if (includeKeys) {
                    if (fieldType.equals(Map.class)) {
                        value = nestedMapOf(jedis, redisKeys);
                    }
                } else {
                    if (redisKeys.size() == 1) {
                        String redisKey = Iterables.getOnlyElement(redisKeys);
                        if (fieldType.equals(char[].class)) {
                            value = jedis.get(redisKey).toCharArray();
                        } else if (fieldType.equals(Character[].class)) {
                            value = ArrayUtils.toObject(jedis.get(redisKey).toCharArray());
                        } else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
                            String toConvert = jedis.get(redisKey);
                            if (toConvert.length() == 1) {
                                value = jedis.get(redisKey).charAt(0);
                            } else {
                                throw new IllegalArgumentException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to %s.", toConvert, redisKey, fieldType));
                            }
                        } else if (fieldType.equals(String.class)) {
                            value = jedis.get(redisKey);
                        } else if (fieldType.equals(byte[].class)) {
                            value = jedis.get(redisKey.getBytes());
                        } else if (fieldType.equals(Byte[].class)) {
                            value = ArrayUtils.toObject(jedis.get(redisKey.getBytes()));
                        } else if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
                            value = Byte.parseByte(jedis.get(redisKey));
                        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                            String toConvert = jedis.get(redisKey);
                            if (Patterns.BOOLEAN.matcher(toConvert).matches()) {
                                value = Patterns.TRUE.matcher(toConvert).matches();
                            } else {
                                throw new IllegalArgumentException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to %s.", toConvert, redisKey, fieldType));
                            }
                        } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
                            String toConvert = jedis.get(redisKey);
                            try {
                                value = Short.parseShort(toConvert);
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to %s.", toConvert, redisKey, fieldType));
                            }
                        } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                            String toConvert = jedis.get(redisKey);
                            try {
                                value = Integer.parseInt(toConvert);
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to %s.", toConvert, redisKey, fieldType));
                            }
                        } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                            String toConvert = jedis.get(redisKey);
                            try {
                                value = Long.parseLong(toConvert);
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to %s.", toConvert, redisKey, fieldType));
                            }
                        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                            String toConvert = jedis.get(redisKey);
                            try {
                                value = Double.parseDouble(toConvert);
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to %s.", toConvert, redisKey, fieldType));
                            }
                        } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                            String toConvert = jedis.get(redisKey);
                            try {
                                value = Float.parseFloat(toConvert);
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to %s.", toConvert, redisKey, fieldType));
                            }
                        } else if (fieldType.equals(Map.class)) {
                            value = jedis.hgetAll(redisKey);
                        } else if (fieldType.equals(List.class) || fieldType.equals(Set.class)) {
                            value = collectionOf(fieldType, jedis, redisKey, alwaysNest);
                        }
                    } else if (redisKeys.size() > 1) {
                        if (fieldType.equals(List.class)) {
                            value = nestedListOf(jedis, redisKeys);
                        }
                    } else {
                        if (!allowNull) {
                           value = nonNullValueOf(fieldType);
                        }
                    }
                }
                return Option.fromNull(value);
            }
        });
    }
}
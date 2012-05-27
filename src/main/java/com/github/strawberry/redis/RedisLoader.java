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
package com.github.strawberry.redis;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.github.strawberry.guice.Redis;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.google.inject.Injector;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import fj.F;
import fj.data.Option;

import static com.github.strawberry.util.JedisUtil.using;
import static com.github.strawberry.util.Types.BOOLEAN;
import static com.github.strawberry.util.Types.TRUE;
import static com.github.strawberry.util.Types.collectionImplementationOf;
import static com.github.strawberry.util.Types.genericTypeOf;
import static com.github.strawberry.util.Types.isEqualTo;
import static com.github.strawberry.util.Types.isAssignableTo;
import static com.github.strawberry.util.Types.mapImplementationOf;

/**
 * {@code RedisLoader} is used in conjunction with a {@link CacheBuilder} to
 * construct a {@link Cache} of {@link Field} values. An {@link Injector} uses
 * these field values during the object-creation phase when setting values for
 * fields that have been annotated with the {@link Redis}-annotation.
 * 
 * @author Wiehann Matthysen
 */
public final class RedisLoader extends CacheLoader<Field, Option> {

    /**
     * Internal enum to match against all supported Redis data types.
     */
    private enum JedisType {
        STRING, HASH, LIST, SET, ZSET
    }

    private final JedisPool pool;

    /**
     * Initializes a newly created {@code RedisLoader} with the given
     * {@link JedisPool} to be used as source for connections to a Redis
     * database.
     * @param pool The pool of connections to a Redis database.
     */
    public RedisLoader(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public Option load(Field field) throws Exception {
        return loadFromRedis(this.pool, field, field.getAnnotation(Redis.class));
    }

    /**
     * Utility method to create a default non-null value for the given type
     * parameter. This gets called when {@link Redis#allowNull()} was set to
     * false for the given {@link Field}, and no value for the specified key(s)
     * (see {@link Redis#value()}) was present in the Redis database.
     * @param type The type to create the non-null value for.
     * @return A non-null instance of the type. Note: for primitives this will
     * obviously result in a boxed return value.
     */
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
        } else if (type.equals(BigInteger.class)) {
            value = BigInteger.ZERO;
        } else if (type.equals(float.class) || type.equals(Float.class)) {
            value = 0.0f;
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            value = 0.0;
        } else if (type.equals(BigDecimal.class)) {
            value = BigDecimal.ZERO;
        } else if (Map.class.isAssignableFrom(type)) {
            value = mapImplementationOf(type);
        } else if (Collection.class.isAssignableFrom(type)) {
            value = collectionImplementationOf(type);
        }
        return value;
    }

    private static Map<?, ?> nestedMapOf(Field field, Jedis jedis, Set<String> redisKeys) {
        Map map = mapImplementationOf(field.getType());
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

    private static Collection<?> nestedCollectionOf(Field field, Jedis jedis, Set<String> redisKeys) {
        Collection collection = collectionImplementationOf(field.getType());
        for (String redisKey : redisKeys) {
            JedisType jedisType = JedisType.valueOf(jedis.type(redisKey).toUpperCase());
            switch (jedisType) {
                case STRING: {
                    collection.add(jedis.get(redisKey));
                } break;
                case HASH: {
                    collection.add(jedis.hgetAll(redisKey));
                } break;
                case LIST: {
                    collection.add(jedis.lrange(redisKey, 0, -1));
                } break;
                case SET: {
                    collection.add(jedis.smembers(redisKey));
                } break;
                case ZSET: {
                    collection.add(jedis.zrange(redisKey, 0, -1));
                } break;
            }
        }
        return collection;
    }
    
    private static Map<?, ?> mapOf(Field field, Jedis jedis, String key) {
        Map map = mapImplementationOf(field.getType());
        JedisType jedisType = JedisType.valueOf(jedis.type(key).toUpperCase());
        switch (jedisType) {
            case STRING: {
                map.put(key, jedis.get(key));
            } break;
            case HASH: {
                Option<Type> valueType = genericTypeOf(field, 1);
                if (valueType.exists(isAssignableTo(Map.class)) || valueType.exists(isEqualTo(Object.class))) {
                    map.put(key, jedis.hgetAll(key));
                } else {
                    map.putAll(jedis.hgetAll(key));
                }
            } break;
            case LIST: {
                map.put(key, jedis.lrange(key, 0, -1));
            } break;
            case SET: {
                map.put(key, jedis.smembers(key));
            } break;
            case ZSET: {
                map.put(key, jedis.zrange(key, 0, -1));
            } break;
        }
        return map;
    }

    private static Collection<?> collectionOf(Field field, Jedis jedis, String key) {
        Collection collection = collectionImplementationOf(field.getType());
        JedisType jedisType = JedisType.valueOf(jedis.type(key).toUpperCase());
        Option<Type> genericType = genericTypeOf(field, 0);
        switch (jedisType) {
            case STRING: {
                collection.add(jedis.get(key));
            } break;
            case HASH: {
                collection.add(jedis.hgetAll(key));
            } break;
            case LIST: {
                if (genericType.exists(isAssignableTo(Collection.class)) || genericType.exists(isEqualTo(Object.class))) {
                    collection.add(jedis.lrange(key, 0, -1));
                } else {
                    collection.addAll(jedis.lrange(key, 0, -1));
                }
            } break;
            case SET: {
                if (genericType.exists(isAssignableTo(Collection.class)) || genericType.exists(isEqualTo(Object.class))) {
                    collection.add(jedis.smembers(key));
                } else {
                    collection.addAll(jedis.smembers(key));
                }
            } break;
            case ZSET: {
                if (genericType.exists(isAssignableTo(Collection.class)) || genericType.exists(isEqualTo(Object.class))) {
                    collection.add(jedis.zrange(key, 0, -1));
                } else {
                    collection.addAll(jedis.zrange(key, 0, -1));
                }
            } break;
        }
        return collection;
    }

    private static Option loadFromRedis(JedisPool pool, final Field field, final Redis annotation) {
        return using(pool)._do(new F<Jedis, Option>() {

            @Override
            public Option f(Jedis jedis) {
                Object value = null;
                
                Class<?> fieldType = field.getType();

                String pattern = annotation.value();
                boolean allowNull = annotation.allowNull();

                Set<String> redisKeys = Sets.newTreeSet(jedis.keys(pattern));
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
                            throw ConversionException.of(toConvert, redisKey, fieldType);
                        }
                    } else if (fieldType.equals(String.class)) {
                        value = jedis.get(redisKey);
                    } else if (fieldType.equals(byte[].class)) {
                        value = jedis.get(redisKey.getBytes());
                    } else if (fieldType.equals(Byte[].class)) {
                        value = ArrayUtils.toObject(jedis.get(redisKey.getBytes()));
                    } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                        String toConvert = jedis.get(redisKey);
                        if (BOOLEAN.matcher(toConvert).matches()) {
                            value = TRUE.matcher(toConvert).matches();
                        } else {
                            throw ConversionException.of(toConvert, redisKey, fieldType);
                        }
                    } else if (Map.class.isAssignableFrom(fieldType)) {
                        value = mapOf(field, jedis, redisKey);
                    } else if (Collection.class.isAssignableFrom(fieldType)) {
                        value = collectionOf(field, jedis, redisKey);
                    } else {
                        String toConvert = jedis.get(redisKey);
                        try {
                            if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
                                value = Byte.parseByte(jedis.get(redisKey));
                            } else if (fieldType.equals(short.class) || fieldType.equals(Short.class)) {
                                value = Short.parseShort(toConvert);
                            } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                                value = Integer.parseInt(toConvert);
                            } else if (fieldType.equals(long.class) || fieldType.equals(Long.class)) {
                                value = Long.parseLong(toConvert);
                            } else if (fieldType.equals(BigInteger.class)) {
                                value = new BigInteger(toConvert);
                            } else if (fieldType.equals(float.class) || fieldType.equals(Float.class)) {
                                value = Float.parseFloat(toConvert);
                            } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                                value = Double.parseDouble(toConvert);
                            } else if (fieldType.equals(BigDecimal.class)) {
                                value = new BigDecimal(toConvert);
                            }
                        } catch (NumberFormatException exception) {
                            throw ConversionException.of(exception, toConvert, redisKey, fieldType);
                        }
                    }
                } else if (redisKeys.size() > 1) {
                    if (Map.class.isAssignableFrom(fieldType)) {
                        value = nestedMapOf(field, jedis, redisKeys);
                    }
                    else if (Collection.class.isAssignableFrom(fieldType)) {
                        value = nestedCollectionOf(field, jedis, redisKeys);
                    }
                } else {
                    if (!allowNull) {
                        value = nonNullValueOf(fieldType);
                    }
                }
                return Option.fromNull(value);
            }
        });
    }
}
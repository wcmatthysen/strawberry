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
package com.github.strawberry.guice.config;

import com.github.strawberry.guice.Config;
import com.github.strawberry.guice.ConversionException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

import com.github.strawberry.guice.Redis;
import com.github.strawberry.redis.RedisLoader;
import static com.github.strawberry.util.Types.BOOLEAN;
import static com.github.strawberry.util.Types.TRUE;
import static com.github.strawberry.util.Types.collectionImplementationOf;
import static com.github.strawberry.util.Types.genericTypeOf;
import static com.github.strawberry.util.Types.isAssignableTo;
import static com.github.strawberry.util.Types.isEqualTo;
import static com.github.strawberry.util.Types.mapImplementationOf;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.inject.Injector;

import redis.clients.jedis.JedisPool;

import fj.data.Option;
import java.lang.reflect.Type;

import java.util.Properties;
import redis.clients.jedis.Jedis;

/**
 * {@code RedisLoader} is used in conjunction with a {@link CacheBuilder} to
 * construct a {@link Cache} of {@link Field} values. An {@link Injector} uses
 * these field values during the object-creation phase when setting values for
 * fields that have been annotated with the {@link Redis}-annotation.
 * 
 * @author Wiehann Matthysen
 */
public final class ConfigLoader extends CacheLoader<Field, Option> {

    private final Properties properties;

    /**
     * Internal enum to match against all supported Redis data types.
     */
    /**
     * Initializes a newly created {@code RedisLoader} with the given
     * {@link JedisPool} to be used as source for connections to a Redis
     * database.
     * @param pool The pool of connections to a Redis database.
     */
    public ConfigLoader(Properties properties) {
        this.properties = properties;
    }

    @Override
    public Option load(Field field) throws Exception {
        return getFromProperties(properties, field, field.getAnnotation(Config.class));
    }


    private static Option getFromProperties(Properties properties, final Field field, final Config annotation) {

        Object value = null;
        
        Class<?> fieldType = field.getType();

        String pattern = annotation.value();
        boolean allowNull = annotation.allowNull();

        Set<String> matchingKeys = getKeys(properties, pattern);
        if (matchingKeys.size() == 1) {
            String matchingKey = Iterables.getOnlyElement(matchingKeys);
            if (fieldType.equals(char[].class)) {
                value = properties.get(matchingKey).toString().toCharArray();
            } else if (fieldType.equals(Character[].class)) {
                value = ArrayUtils.toObject(properties.get(matchingKey).toString().toCharArray());
            } else if (fieldType.equals(char.class) || fieldType.equals(Character.class)) {
                String toConvert = properties.get(matchingKey).toString();
                if (toConvert.length() == 1) {
                    value = properties.get(matchingKey).toString().charAt(0);
                } else {
                    throw ConversionException.of(toConvert, matchingKey, fieldType);
                }
            } else if (fieldType.equals(String.class)) {
                value = properties.get(matchingKey);
            } else if (fieldType.equals(byte[].class)) {
                if (properties.containsKey(matchingKey)) {
                    value = properties.get(matchingKey).toString().getBytes();
                } else {
                    value = null;
                }
            } else if (fieldType.equals(Byte[].class)) {
                value = ArrayUtils.toObject(properties.get(matchingKey).toString().getBytes());
            } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                String toConvert = properties.get(matchingKey).toString();
                if (BOOLEAN.matcher(toConvert).matches()) {
                    value = TRUE.matcher(toConvert).matches();
                } else {
                    throw ConversionException.of(toConvert, matchingKey, fieldType);
                }
            } else if (Map.class.isAssignableFrom(fieldType)) {
                value = mapOf(field, properties, matchingKey);
            } else if (Collection.class.isAssignableFrom(fieldType)) {
                value = collectionOf(field, properties, matchingKey);
            } else {
                String toConvert = properties.get(matchingKey).toString();
                try {
                    if (fieldType.equals(byte.class) || fieldType.equals(Byte.class)) {
                        value = Byte.parseByte(properties.get(matchingKey).toString());
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
                    throw ConversionException.of(exception, toConvert, matchingKey, fieldType);
                }
            }
        } else if (matchingKeys.size() > 1) {
            if (Map.class.isAssignableFrom(fieldType)) {
                value = nestedMapOf(field, properties, matchingKeys);
            }
            else if (Collection.class.isAssignableFrom(fieldType)) {
                value = nestedCollectionOf(field, properties, matchingKeys);
            }
        } else {
            if (!allowNull) {
                value = nonNullValueOf(fieldType);
            }
        }
        return Option.fromNull(value);
    }

    private static Set<String> getKeys(Properties properties, String pattern) {
        if (properties.containsKey(pattern)) {
            return Sets.newHashSet(pattern);
        } else {
            return Sets.newHashSet();
        }
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

    private static Map<?, ?> nestedMapOf(Field field, Properties properties, Set<String> redisKeys) {
        Map map = mapImplementationOf(field.getType());
        for (String redisKey : redisKeys) {
            map.put(redisKey, properties.get(redisKey));
        }
        return map;
    }

    private static Collection<?> nestedCollectionOf(Field field, Properties properties, Set<String> redisKeys) {
        Collection collection = collectionImplementationOf(field.getType());
        for (String redisKey : redisKeys) {
            collection.add(properties.get(redisKey));
        }
        return collection;
    }
    
    private static Map<?, ?> mapOf(Field field, Properties properties, String key) {
        Map map = mapImplementationOf(field.getType());
        map.put(key, properties.get(key));
        return map;
    }

    private static Collection<?> collectionOf(Field field, Properties properties, String key) {
        Collection collection = collectionImplementationOf(field.getType());
        Object list = properties.get(key);
        if (list != null) {
            collection.addAll(Lists.newArrayList(properties.get(key).toString().split(",")));
            return collection;
        } else {
            return collection;
        }
    }

}

package org.strawberry.redis;

import org.strawberry.util.Patterns;
import org.apache.commons.lang3.ArrayUtils;
import com.google.common.cache.CacheLoader;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import fj.F;
import fj.data.Option;
import java.lang.reflect.Field;
import java.util.Collections;
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
public class RedisLoader extends CacheLoader<Field, Option> {

    private static final String STRING = "string";
    private static final String HASH = "hash";
    private static final String LIST = "list";
    
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
        if (type.equals(String.class)) {
            value = "";
        } else if (type.equals(byte[].class)) {
            value = new byte[]{};
        } else if (type.equals(Byte[].class)) {
            value = new Byte[]{};
        } else if (type.equals(boolean.class) || type.equals(Boolean.class)) {
            value = false;
        } else if (type.equals(int.class) || type.equals(Integer.class)) {
            value = 0;
        } else if (type.equals(double.class) || type.equals(Double.class)) {
            value = 0.0;
        } else if (type.equals(Map.class)) {
            value = Collections.EMPTY_MAP;
        } else if (type.equals(List.class)) {
            value = Collections.EMPTY_LIST;
        }
        return value;
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
                        Map<String, Object> map = Maps.newLinkedHashMap();
                        for (String redisKey : redisKeys) {
                            if (jedis.type(redisKey).equals(STRING)) {
                                map.put(redisKey, jedis.get(redisKey));
                            } else if (jedis.type(redisKey).equals(HASH)) {
                                map.put(redisKey, jedis.hgetAll(redisKey));
                            } else if (jedis.type(redisKey).equals(LIST)) {
                                map.put(redisKey, jedis.lrange(redisKey, 0, -1));
                            }
                        }
                        value = map;
                    }
                } else {
                    if (redisKeys.size() == 1) {
                        String redisKey = Iterators.getOnlyElement(redisKeys.iterator());
                        if (fieldType.equals(String.class)) {
                            value = jedis.get(redisKey);
                        } else if (fieldType.equals(byte[].class)) {
                            value = jedis.get(redisKey.getBytes());
                        } else if (fieldType.equals(Byte[].class)) {
                            value = ArrayUtils.toObject(jedis.get(redisKey.getBytes()));
                        } else if (fieldType.equals(boolean.class) || fieldType.equals(Boolean.class)) {
                            String toConvert = jedis.get(redisKey);
                            if (Patterns.BOOLEAN.matcher(toConvert).matches()) {
                                value = Patterns.TRUE.matcher(toConvert).matches();
                            } else {
                                throw new IllegalArgumentException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to boolean.", toConvert, redisKey));
                            }
                        } else if (fieldType.equals(int.class) || fieldType.equals(Integer.class)) {
                            String toConvert = jedis.get(redisKey);
                            try {
                                value = Integer.parseInt(toConvert);
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to integer.", toConvert, redisKey));
                            }
                        } else if (fieldType.equals(double.class) || fieldType.equals(Double.class)) {
                            String toConvert = jedis.get(redisKey);
                            try {
                                value = Double.parseDouble(toConvert);
                            } catch (NumberFormatException e) {
                                throw new NumberFormatException(String.format(
                                        "Cannot convert value: (%s) at key: (%s) to double.", toConvert, redisKey));
                            }
                        } else if (fieldType.equals(Map.class)) {
                            value = jedis.hgetAll(redisKey);
                        } else if (fieldType.equals(List.class)) {
                            if (jedis.type(redisKey).equals(STRING)) {
                                value = Lists.newArrayList(jedis.get(redisKey));
                            } else if (jedis.type(redisKey).equals(HASH)) {
                                value = Lists.newArrayList(jedis.hgetAll(redisKey));
                            } else if (jedis.type(redisKey).equals(LIST)) {
                                if (alwaysNest) {
                                    List<List<String>> nestedList = Lists.newArrayList();
                                    nestedList.add(jedis.lrange(redisKey, 0, -1));
                                    value = nestedList;
                                } else {
                                    value = jedis.lrange(redisKey, 0, -1);
                                }
                            }
                        }
                    } else if (redisKeys.size() > 1) {
                        if (fieldType.equals(List.class)) {
                            List<Object> list = Lists.newArrayList();
                            for (String redisKey : redisKeys) {
                                if (jedis.type(redisKey).equals(STRING)) {
                                    list.add(jedis.get(redisKey));
                                } else if (jedis.type(redisKey).equals(HASH)) {
                                    list.add(jedis.hgetAll(redisKey));
                                } else if (jedis.type(redisKey).equals(LIST)) {
                                    list.add(jedis.lrange(redisKey, 0, -1));
                                }
                            }
                            value = list;
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
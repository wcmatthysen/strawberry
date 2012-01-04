package org.strawberry.util;

import fj.Effect;
import fj.F;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

/**
 *
 * @author Wiehann Matthysen
 */
public final class JedisUtil {

    private JedisUtil() {}

    public static JedisPool destroyOnShutdown(final JedisPool pool) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                pool.destroy();
            }
        });
        return pool;
    }

    public static interface LinkedCallbackBuilder {

        public <T> T _do(F<Jedis, T> callback);

        public <T> void _do(Effect<Jedis> callback);
    }

    public static <T> LinkedCallbackBuilder using(final JedisPool pool) {
        return new LinkedCallbackBuilder() {

            @Override
            public <T> T _do(F<Jedis, T> callback) {
                T result = null;
                boolean returned = false;
                Jedis jedis = pool.getResource();
                try {
                    result = callback.f(jedis);
                } catch (JedisConnectionException e) {
                    pool.returnBrokenResource(jedis);
                    returned = true;
                    throw e;
                } finally {
                    if (!returned) {
                        pool.returnResource(jedis);
                    }
                }
                return result;
            }

            @Override
            public <T> void _do(Effect<Jedis> callback) {
                boolean returned = false;
                Jedis jedis = pool.getResource();
                try {
                    callback.e(jedis);
                } catch (JedisConnectionException e) {
                    pool.returnBrokenResource(jedis);
                    returned = true;
                    throw e;
                } finally {
                    if (!returned) {
                        pool.returnResource(jedis);
                    }
                }
            }
        };
    }
}

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
package org.strawberry.util;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import fj.Effect;
import fj.F;

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

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
package com.github.strawberry.util;

import org.junit.Test;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import fj.Effect;
import fj.F;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsSame.sameInstance;
import static org.junit.Assert.assertThat;

import static com.github.strawberry.util.JedisUtil.using;

/**
 *
 * @author Wiehann Matthysen
 */
public class JedisUtilTest {
    
    private static class CustomException extends RuntimeException {
        
        private final Jedis jedis;
        
        public CustomException(Jedis jedis) {
            this.jedis = jedis;
        }
    }

    @Test
    public void test_that_connection_is_returned_to_pool() {
        JedisPoolConfig config = new JedisPoolConfig();
        
        // Ensure that only a single connection is active in pool.
        config.setMaxActive(1);
        
        final JedisPool pool = new JedisPool(config, "localhost", 6379);
        
        // Use pool with Effect.
        using(pool)._do(new Effect<Jedis>(){

            @Override
            public void e(Jedis connection) {
                // Do nothing.
            }
        });
        
        // Ensure that connection was returned, by retrieving the only connection in pool.
        Jedis connection = pool.getResource();
        assertThat(connection.isConnected(), is(true));
        pool.returnResource(connection);
        
        // Use pool with F.
        Jedis returnedConnection = using(pool)._do(new F<Jedis, Jedis>() {

            @Override
            public Jedis f(Jedis connection) {
                return connection;
            }
        });
        
        // Ensure that connection was returned, by retrieving the only connection in pool.
        connection = pool.getResource();
        assertThat(connection.isConnected(), is(true));
        assertThat(connection, is(sameInstance(returnedConnection)));
        pool.returnResource(connection);
    }
    
    @Test
    public void test_that_connection_is_returned_to_pool_when_exception_occurs() {
        JedisPoolConfig config = new JedisPoolConfig();
        
        // Ensure that only a single connection is active in pool.
        config.setMaxActive(1);
        
        final JedisPool pool = new JedisPool(config, "localhost", 6379);
        
        // Use pool with Effect.
        Jedis returnedConnection = null;
        try {
            using(pool)._do(new Effect<Jedis>(){

                @Override
                public void e(Jedis connection) {
                    throw new CustomException(connection);
                }
            });
        } catch (CustomException e) {
            returnedConnection = e.jedis;
        }
        
        // Ensure that connection was returned, by retrieving the only connection in pool.
        Jedis connection = pool.getResource();
        assertThat(connection.isConnected(), is(true));
        assertThat(connection, is(sameInstance(returnedConnection)));
        pool.returnResource(connection);
        
        // Use pool with F.
        returnedConnection = null;
        try {
            using(pool)._do(new F<Jedis, Jedis>() {

                @Override
                public Jedis f(Jedis connection) {
                    throw new CustomException(connection);
                }
            });
        } catch (CustomException e) {
            returnedConnection = e.jedis;
        }
        
        // Ensure that connection was returned, by retrieving the only connection in pool.
        connection = pool.getResource();
        assertThat(connection.isConnected(), is(true));
        assertThat(connection, is(sameInstance(returnedConnection)));
        pool.returnResource(connection);
    }
}

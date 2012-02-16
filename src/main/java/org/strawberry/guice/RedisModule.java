/**
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
package org.strawberry.guice;

import java.lang.reflect.Field;

import org.strawberry.redis.RedisLoader;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

import redis.clients.jedis.JedisPool;

import fj.data.Option;

/**
 *
 * @author Wiehann Matthysen
 */
public final class RedisModule extends AbstractModule {

    private final Cache<Field, Option> cache;

    public RedisModule(Cache<Field, Option> cache) {
        this.cache = cache;
    }

    public RedisModule(JedisPool pool) {
        this.cache = CacheBuilder.newBuilder().maximumSize(0).build(new RedisLoader(pool));
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new RedisTypeListener(this.cache));
    }
}

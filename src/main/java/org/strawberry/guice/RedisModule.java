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

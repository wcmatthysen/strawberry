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
package com.github.strawberry.guice;

import com.github.strawberry.guice.config.ConfigLoader;
import java.lang.reflect.Field;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.inject.AbstractModule;
import com.google.inject.Module;
import com.google.inject.matcher.Matchers;

import fj.data.Option;
import java.util.Properties;

/**
 * {@code RedisModule} is responsible for setting up the custom injections that
 * need to occur for the {@link Redis} field-annotations that may be present in
 * your classes. You simply need to install this {@link Module} inside your main
 * Guice {@code Module}, giving it the {@link JedisPool} of connections to your
 * Redis database as constructor argument. This is illustrated with the
 * following example:
 * 
 * <pre>
 * public class MyCustomModule extends AbstractModule {
 *   
 *   private final JedisPool pool = new JedisPool("localhost", 6379);
 * 
 *   {@literal @}Override
 *   protected void configure() {
 *     install(new RedisModule(this.pool));
 *     // set up the rest of your bindings.
 *     ...
 *   }
 * }
 * </pre>
 * 
 * However, this assumes that you don't want any caching to occur for the field
 * values that are loaded from the Redis database. For more fine-grained control
 * you can manually provide the {@link LoadingCache} of {@link Field}-to-value
 * mappings as the following example illustrates:
 * 
 * <pre>
 * public class MyCustomModule extends AbstractModule {
 *   
 *   private final JedisPool pool = new JedisPool("localhost", 6379);
 * 
 *   {@literal @}Override
 *   protected void configure() {
 *     LoadingCache<Field, Option> cache =
 *       CacheBuilder.newBuilder().
 *       expireAfterWrite(1, TimeUnit.DAYS).
 *       maximumSize(1000).
 *       build(new RedisLoader(this.pool));
 *     install(new RedisModule(cache));
 *     // set up the rest of your bindings.
 *     ...
 *   }
 * }
 * </pre>
 * 
 * The abovementioned example will create a cache that stores up to 1000 field
 * values, and will expire these values after a day before loading them again
 * from the Redis database. See Google Guava's {@link CacheBuilder} class for
 * more comprehensive examples on how to construct a customized cache.
 * <b>Note</b>: the {@code CacheBuilder} requires a {@link CacheLoader} to
 * actually load the values into the cache if no matching key-value pair could
 * be found. The class responsible for this is {@link RedisLoader} and contains
 * the bulk of the logic that pertains to loading and transforming values from
 * Redis to be used as field-values.
 * 
 * @author Wiehann Matthysen
 */
public final class ConfigModule extends AbstractModule {

    private final LoadingCache<Field, Option> cache;

    /**
     * Initializes a newly created {@code RedisModule} with the given cache of
     * {@code Field}-to-value mappings.
     * @param cache The cache that will serve as storage for field values that
     * are loaded from the Redis database.
     */
    public ConfigModule(LoadingCache<Field, Option> cache) {
        this.cache = cache;
    }

    /**
     * Initializes a newly created {@code RedisModule} with the given
     * {@code JedisPool} of connections to a Redis database. 
     * @param pool The pool of connections to a Redis database.
     */
    public ConfigModule(Properties properties) {
        
        // This constructor would be called in situations where caching of
        // Redis field-values are not important, and the latest most up-to-date
        // values should be retrieved from Redis whevever the Injector creates
        // an object with Redis-annotated fields.
        
        // It achieves this by creating a cache that never stores it's values
        // (maximum size of 0).
        this.cache = CacheBuilder.newBuilder().maximumSize(0).build(new ConfigLoader(properties));
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new ConfigTypeListener(this.cache));
    }
}

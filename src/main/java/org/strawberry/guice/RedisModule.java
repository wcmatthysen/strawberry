package org.strawberry.guice;

import com.google.common.cache.Cache;
import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

/**
 *
 * @author Wiehann Matthysen
 */
public class RedisModule extends AbstractModule {

    private final Cache cache;

    public RedisModule(Cache cache) {
        this.cache = cache;
    }

    @Override
    protected void configure() {
        bindListener(Matchers.any(), new RedisTypeListener(this.cache));
    }
}

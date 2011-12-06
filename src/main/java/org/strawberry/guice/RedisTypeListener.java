package org.strawberry.guice;

import com.google.common.cache.Cache;
import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeEncounter;
import com.google.inject.spi.TypeListener;
import fj.data.Option;
import java.lang.reflect.Field;

/**
 *
 * @author Wiehann Matthysen
 */
final class RedisTypeListener implements TypeListener {

    private final Cache<Field, Option> cache;

    RedisTypeListener(Cache<Field, Option> cache) {
        this.cache = cache;
    }

    @Override
    public <T> void hear(TypeLiteral<T> typeLiteral, TypeEncounter<T> typeEncounter) {
        for (Field field : typeLiteral.getRawType().getDeclaredFields()) {
            if (field.isAnnotationPresent(Redis.class)) {
                typeEncounter.register(new RedisMembersInjector<T>(this.cache, field));
            }
        }
    }
}

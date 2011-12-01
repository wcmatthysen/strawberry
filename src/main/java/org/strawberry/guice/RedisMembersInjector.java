package org.strawberry.guice;

import com.google.common.cache.Cache;
import com.google.inject.MembersInjector;
import fj.data.Option;
import java.lang.reflect.Field;

/**
 *
 * @author Wiehann Matthysen
 */
public class RedisMembersInjector<T> implements MembersInjector<T> {

    private final Cache<Field, Option> cache;
    private final Field field;

    RedisMembersInjector(Cache<Field, Option> cache, Field field) {
        this.cache = cache;
        this.field = field;
        this.field.setAccessible(true);
    }

    @Override
    public void injectMembers(final T object) {
        Option value = this.cache.getUnchecked(this.field);
        try {
            this.field.set(object, value.toNull());
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

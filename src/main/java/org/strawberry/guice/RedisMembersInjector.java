package org.strawberry.guice;

import java.lang.reflect.Field;

import com.google.common.cache.Cache;
import com.google.inject.MembersInjector;

import fj.data.Option;

/**
 *
 * @author Wiehann Matthysen
 */
final class RedisMembersInjector<T> implements MembersInjector<T> {

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
            Redis annotation = this.field.getAnnotation(Redis.class);
            if (this.field.get(object) != null) {
                // If field is not equal to null (i.e. default value has been set)
                // and if value to be injected is not null, then set.
                // Or, if forced update has been specified, then set.
                if (annotation.forceUpdate() || value.isSome()) {
                    this.field.set(object, value.toNull());
                }
            } else {
                // Always set null field.
                this.field.set(object, value.toNull());
            }
        } catch (IllegalArgumentException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

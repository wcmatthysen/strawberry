package org.strawberry.redis;

/**
 *
 * @author Wiehann Matthysen
 */
public final class ConversionException extends RuntimeException {

    ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    ConversionException(String message) {
        super(message);
    }

    static ConversionException of(Exception cause, String toConvert, String key, Class<?> type) {
        return new ConversionException(String.format("Cannot convert value: (%s) at key: (%s) to %s.", toConvert, key, type), cause);
    }

    static ConversionException of(String toConvert, String key, Class<?> type) {
        return new ConversionException(String.format("Cannot convert value: (%s) at key: (%s) to %s.", toConvert, key, type));
    }
}

package org.strawberry.util;

import java.util.regex.Pattern;

/**
 *
 * @author Wiehann Matthysen
 */
public final class Patterns {

    private Patterns() {}

    public static final Pattern BOOLEAN = Pattern.compile("^t|true|y|yes|1|f|false|n|no|0$", Pattern.CASE_INSENSITIVE);

    public static final Pattern TRUE = Pattern.compile("^t|true|y|yes|1$", Pattern.CASE_INSENSITIVE);
}

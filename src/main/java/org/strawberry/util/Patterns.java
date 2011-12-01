package org.strawberry.util;

import java.util.regex.Pattern;

/**
 *
 * @author Wiehann Matthysen
 */
public final class Patterns {
    
    private Patterns() {}
    
    public static final Pattern INTEGER = Pattern.compile("^[-]?\\d+$");
    
    public static final Pattern BOOLEAN = Pattern.compile("^t|true|y|yes|f|false|n|no$", Pattern.CASE_INSENSITIVE);
    
    public static final Pattern TRUE = Pattern.compile("^t|true|y|yes$", Pattern.CASE_INSENSITIVE);
}

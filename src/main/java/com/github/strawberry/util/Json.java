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
package com.github.strawberry.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author nicok
 */
public class Json {
    static JsonParser parser = new JsonParser();

    public static Object parsePrimitive(JsonElement e) {
        JsonPrimitive p = e.getAsJsonPrimitive();
        if (p.isString()) {
            return e.getAsString();
        }
        if (p.isBoolean()) {
            return e.getAsBoolean();
        }
        if (p.isNumber()) {
            return e.getAsInt();
        }
        return p.getAsString();
    }
    public static Collection parseArray(String json) {
        JsonArray o = (JsonArray)parser.parse(json);
        Collection c = Lists.newArrayList();
        for (JsonElement value : o) {
            if (!value.isJsonPrimitive()) {
                if (value.isJsonArray()) {
                    c.add(parseArray(value.toString()));
                } else if (value.isJsonObject()) {
                    c.add(parse(value.toString()));
                }

            } else {
                c.add(parsePrimitive(value));

            }
        }
        return c;
    }
    public static Map<String, Object> parse(String json) {
        JsonObject o = (JsonObject)parser.parse(json);
        Set<Map.Entry<String,JsonElement>> set = o.entrySet();
        Map<String,Object> map = Maps.newHashMap();
        for (Map.Entry<String,JsonElement> e : set) {
            String key = e.getKey();
            JsonElement value = e.getValue();
            if (!value.isJsonPrimitive()) {
                if (value.isJsonObject()) {
                    map.put(key, parse(value.toString()));

                } else if (value.isJsonArray()) {
                    map.put(key, parseArray(value.toString()));

                }
            } else {
                map.put(key, parsePrimitive(value));
            }
            
        }
        return map;

    }

    
}

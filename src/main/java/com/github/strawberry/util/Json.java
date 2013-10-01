/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

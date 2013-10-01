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
package com.github.strawberry.guice.json;

import com.github.strawberry.guice.Config;
import com.github.strawberry.guice.ConfigModule;
import com.github.strawberry.util.Json;
import com.google.common.collect.Lists;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.util.List;
import java.util.Map;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

/**
 *
 * @author nicok
 */
public class JsonTest extends AbstractModule {;

    private Injector injector;
    
    @Override
    protected void configure() {
        String json = "{\"mynumber\":1,\"mystring\":\"My String\",\"mylist\":[1,2,3]}";
        Map m = Json.parse(json);
        install(new ConfigModule(m));
    }

    @Before
    public void setup() {
        this.injector = Guice.createInjector(this);
    }

    @After
    public void teardown() {
    }

     public static class Container {

        @Config(value = "mynumber", allowNull = false)
        Integer mynumber;

        @Config(value = "mystring", allowNull = false)
        String mystring;

        @Config(value = "mylist", allowNull = false)
        List<Integer> mylist;

    }
 
    @Test
    public void testJson() {
        Container c = injector.getInstance(Container.class);

        assertThat(c.mystring, is("My String"));
        assertThat(c.mynumber, is(1));
        List<Integer> expectedList = Lists.newArrayList(1,2,3);
        assertThat(c.mylist, is(equalTo(expectedList)));

    }
}

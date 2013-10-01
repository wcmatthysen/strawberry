/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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

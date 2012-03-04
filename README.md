strawberry
==========
A lightweight Java utility library that utilizes [Google Guice](http://code.google.com/p/google-guice) to inject data-structures from a [Redis](http://redis.io) database into object instance fields. The name of the library was roughly constructed as follows: _Red_(is) + _Guice_ = _Strawberry_(-juice). This library should not be seen as an alternative to [Johm](https://github.com/xetorthio/johm) (a very powerful Object-Hash Mapping library) but rather a compliment. Johm is designed for storing (and retrieving) Java objects in Redis, whereas strawberry's main focus is on reading (and caching) configuration values from Redis and injecting these values (which could be list or map structures) into object instance fields.

Installing with Maven
---------------------
To use strawberry with [Maven](http://maven.apache.org) 2/3, you must add the following repository to your project's `pom.xml` file:

```xml
<repository>
  <id>strawberry-repo</id>
  <name>strawberry repository on GitHub</name>
  <url>http://wcmatthysen.github.com/strawberry/repository/</url>
</repository>
```

Then, you can add the following dependency:

```xml
<dependency>
  <groupId>com.github</groupId>
  <artifactId>strawberry</artifactId>
  <version>1.0.0</version>
</dependency>
```

Getting Started
---------------
The following section assumes that you have access to Redis on your own machine (or another machine on your LAN). For Linux users the [installation instructions](http://redis.io/download) are listed on the Redis website, whereas Windows users will need to put in some additional [effort](http://suretalent.blogspot.com/2011/11/installing-redis-database-as-windows.html) in getting Redis to run natively.

To start off we are going to populate Redis with a couple of configuration values. Let's assume that you have a string, double, integer and boolean configuration value stored at keys _config:string_, _config:double_, _config:int_, _config:boolean_ respectively. You can use _redi-cli_ to set these values by executing the following commands:

    redis 127.0.0.1:6379> set config:string "test value"
    OK
    redis 127.0.0.1:6379> set config:double "0.123456"
    OK
    redis 127.0.0.1:6379> set config:int "123456"
    OK
    redis 127.0.0.1:6379> set config:boolean "True"
    OK

Next, we assume that you have a class named _ConfigStore_. Suppose that this class serves as storage mechanism for all configuration values in your application. The first step in populating ConfigStore's fields with values from the Redis database would be to make use of the _@Redis_-annotation as the following code-snippet shows:

```java
import com.github.strawberry.guice.Redis;

public class ConfigStore {

    @Redis("config:string")
    private String stringValue;

    @Redis("config:double")
    private double doubleValue;

    @Redis("config:int")
    private int intValue;

    @Redis("config:boolean")
    private boolean booleanValue;

    public String getStringValue() {
        return this.stringValue;
    }
 
    public double getDoubleValue() {
        return this.doubleValue;
    }
    
    public int getIntValue() {
        return this.intValue;
    }
 
    public boolean getBooleanValue() {
        return this.booleanValue;
    }
}
```

Next, we need to install _RedisModule_ in one of our Google Guice modules (or use it as our main module). The RedisModule class that ships with strawberry is responsible for setting up the custom injections that need to occur for the @Redis-annotated fields in ConfigStore (or any other class that have @Redis-annotated fields). RedisModule expects a _JedisPool_ (pool of [Jedis](https://github.com/xetorthio/jedis) connections to a Redis database) as constructor argument.

Thus, assuming that we have our main module _MyCustomModule_ containing bindings for our application, we can install RedisModule as follows:

```java
import com.github.strawberry.guice.RedisModule;
import com.google.inject.AbstractModule;
import redis.clients.jedis.JedisPool;

public class MyCustomModule extends AbstractModule {

    private final JedisPool pool = new JedisPool("localhost", 6379);

    @Override
    protected void configure() {
        install(new RedisModule(this.pool));
        // set up the rest of your bindings here.
    }
}
```

Finally, we can provide this module to our main _Injector_ and let Guice inject the field values for our ConfigStore instance as the following code-snippet shows:

```java
import com.google.inject.Guice;
import com.google.inject.Injector;

public class MyApplication {
    
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new MyCustomModule());
        ConfigStore config = injector.getInstance(ConfigStore.class);
        
        System.out.println(config.getStringValue());
        System.out.println(config.getDoubleValue());
        System.out.println(config.getIntValue());
        System.out.println(config.getBooleanValue());
    }
}
```

If you run this application you should see the following output:

    test value
    0.123456
    123456
    true

Where to go from here?
----------------------
For more complex examples (such as aggregate field or complex data-structure injection) you can consult the Wiki-pages. The test cases also provides for usage examples.

License
-------
Strawberry Library
Copyright (C) 2011 - 2012

This library is free software; you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation; either version 3 of the License, or
(at your option) any later version.

This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this library; if not, see <http://www.gnu.org/licenses/>.

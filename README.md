# Bean Proxy

The tool for creation bean proxies.

## How to get it?

You can use it as a maven dependency:

```xml
<dependency>
    <groupId>org.jmmo</groupId>
    <artifactId>bean-proxy</artifactId>
    <version>1.0</version>
</dependency>
```

Or download the latest build at:
    https://github.com/megaprog/bean-proxy/releases

## How to use it?

Let we have the interface

```java
public interface Example {

    int getIntValue();

    void setIntValue(int value);
}
```
   
And some map

```java
Map<String, Object> values = new HashMap<>();
values.put("intValue", 1);
```

We need to create Example class instance with properties corresponded to values map. Gotcha!

```java
Example example = BeanProxy.create(Example.class, values);
```

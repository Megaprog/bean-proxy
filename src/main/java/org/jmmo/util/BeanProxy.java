package org.jmmo.util;

import java.util.Map;

public class BeanProxy {
    private BeanProxy() {}

    public static <T> T create(Class<T> interfaceClass) {
        return builder(interfaceClass).build();
    }

    public static <T> T create(Class<T> interfaceClass, Map<String, Object> source) {
        return builder(interfaceClass).setSource(source).build(interfaceClass);
    }

    public static BeanProxyBuilder builder() {
        return new BeanProxyBuilder<>();
    }

    public static <T> BeanProxyBuilder<T> builder(Class<T> interfaceClass) {
        return new BeanProxyBuilder<T>().setInterfaces(interfaceClass);
    }

    public static <T> BeanProxyBuilder<T> builder(Class<T> interfaceClass, Class<?> ...moreInterfaces) {
        final Class<?>[] interfaces = new Class<?>[moreInterfaces.length + 1];
        interfaces[0] = interfaceClass;
        System.arraycopy(moreInterfaces, 0, interfaces, 1, moreInterfaces.length);
        return new BeanProxyBuilder<T>().setInterfaces(interfaces);
    }
}

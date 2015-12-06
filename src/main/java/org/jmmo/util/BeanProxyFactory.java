package org.jmmo.util;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public interface BeanProxyFactory {

    Proxy createProxy(BiFunction<String, Class<?>, Object> getterHandler, BiConsumer<String, Object> setterHandler, BiFunction<Method, Object[], Object> methodHandler, Class<?>... moreInterfaces);
}

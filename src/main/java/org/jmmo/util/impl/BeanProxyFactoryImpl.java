package org.jmmo.util.impl;

import org.jmmo.util.BeanProxyFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class BeanProxyFactoryImpl implements BeanProxyFactory {

    @Override
    public Proxy createProxy(BiFunction<String, Class<?>, Object> getterHandler, BiConsumer<String, Object> setterHandler, BiFunction<Method, Object[], Object> methodHandler, Class<?>... interfaces) {
        return (Proxy) Proxy.newProxyInstance(getClass().getClassLoader(), interfaces, new InvocationHandlerImpl(getterHandler, setterHandler, methodHandler));
    }
}

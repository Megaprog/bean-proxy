package org.jmmo.util.impl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class InvocationHandlerImpl implements InvocationHandler {
    private final BiFunction<String, Class<?>, Object> getterHandler;
    private final BiConsumer<String, Object> setterHandler;
    private final BiFunction<Method, Object[], Object> methodHandler;

    public InvocationHandlerImpl(BiFunction<String, Class<?>, Object> getterHandler, BiConsumer<String, Object> setterHandler, BiFunction<Method, Object[], Object> regularHandler) {
        this.getterHandler = getterHandler;
        this.setterHandler = setterHandler;
        this.methodHandler = regularHandler;
    }

    public BiFunction<String, Class<?>, Object> getGetterHandler() {
        return getterHandler;
    }

    public BiConsumer<String, Object> getSetterHandler() {
        return setterHandler;
    }

    public BiFunction<Method, Object[], Object> getMethodHandler() {
        return methodHandler;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final String getterProperty = getterProperty(method);
        if (getterProperty != null) {
            return getGetterHandler().apply(getterProperty, method.getReturnType());
        }

        final String setterProperty = setterProperty(method);
        if (setterProperty != null) {
            getSetterHandler().accept(setterProperty, args[0]);
            return null;
        }

        return getMethodHandler().apply(method, args);
    }

    protected String getterProperty(Method method) {
        if (method.getParameterTypes().length != 0 || method.getReturnType() == void.class) {
            return null;
        }

        if (method.getName().startsWith("get")) {
            return propertyName(method.getName(), 3);
        } else if (method.getName().startsWith("is") && (method.getReturnType() == boolean.class || method.getReturnType() == Boolean.class)) {
            return propertyName(method.getName(), 2);
        }

        return null;
    }

    protected String setterProperty(Method method) {
        if (method.getParameterTypes().length == 1 && method.getReturnType() == void.class && method.getName().startsWith("set")) {
            return propertyName(method.getName(), 3);
        }

        return null;
    }

    protected String propertyName(String s, int from) {
        final int to = from + 1;
        return s.substring(from, to).toLowerCase() + s.substring(to);
    }
}

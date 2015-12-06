package org.jmmo.util;

import org.jmmo.util.impl.BeanProxyFactoryImpl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class BeanProxyBuilder<T> implements Cloneable {
    private BeanProxyFactory beanProxyFactory = new BeanProxyFactoryImpl();
    private Class<?>[] interfaces;
    private Map<String, Object> source = new HashMap<>();
    private Map<String, String> mapping;
    private boolean defaultMissingValues = true;
    private boolean readOnly;
    private boolean supportRegularMethods;

    public Class<?>[] getInterfaces() {
        return interfaces;
    }

    public BeanProxyBuilder<T> setInterfaces(Class<?> ...interfaces) {
        this.interfaces = interfaces;
        return this;
    }

    public Map<String, Object> getSource() {
        return source;
    }

    public BeanProxyBuilder<T> setSource(Map<String, Object> source) {
        this.source = source;
        return this;
    }

    public BeanProxyBuilder<T> addValue(String property, Object value) {
        getSource().put(property, value);

        return this;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public BeanProxyBuilder<T> setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
        return this;
    }

    public BeanProxyBuilder<T> addMapping(String beanProperty, String sourceProperty) {
        if (getMapping() == null) {
            setMapping(new HashMap<>());
        }

        getMapping().put(beanProperty, sourceProperty);

        return this;
    }

    public boolean isDefaultMissingValues() {
        return defaultMissingValues;
    }

    public BeanProxyBuilder<T> setDefaultMissingValues(boolean defaultMissingValues) {
        this.defaultMissingValues = defaultMissingValues;
        return this;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public BeanProxyBuilder<T> setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
        return this;
    }

    public boolean isSupportRegularMethods() {
        return supportRegularMethods;
    }

    public BeanProxyBuilder<T> setSupportRegularMethods(boolean supportRegularMethods) {
        this.supportRegularMethods = supportRegularMethods;
        return this;
    }

    public BeanProxyFactory getBeanProxyFactory() {
        return beanProxyFactory;
    }

    public BeanProxyBuilder<T> setBeanProxyFactory(BeanProxyFactory beanProxyFactory) {
        this.beanProxyFactory = beanProxyFactory;
        return this;
    }

    public T build() {
        if (getInterfaces() == null || getInterfaces().length == 0) {
            throw new IllegalStateException("Proxy interfaces must be specified");
        }
        if (getSource() == null) {
            throw new IllegalStateException("The source map must be specified");
        }

        //noinspection unchecked
        return (T) getBeanProxyFactory().createProxy(getterHandler(), setterHandler(), methodHandler(), getInterfaces());
    }

    public <I> I build(Class<I> interfaceClass) {
        return interfaceClass.cast(build());
    }

    @Override
    public BeanProxyBuilder<T> clone() {
        try {
            final BeanProxyBuilder clone = (BeanProxyBuilder) super.clone();
            if (getSource() != null) {
                clone.setSource(new HashMap<>(getSource()));
            }
            if (getMapping() != null) {
                clone.setMapping(new HashMap<>(getMapping()));
            }
            return clone;
        } catch (CloneNotSupportedException cannotHappen) {
            throw new AssertionError();
        }
    }

    protected BiFunction<String, Class<?>, Object> getterHandler() {
        final BiFunction<String, Class<?>, Object> getterHandler = (s, aClass) -> {
            if (isDefaultMissingValues()) {
                final Object value = getSource().get(s);
                if (value == null) {
                    return defaultValue(aClass);
                }

                return value;
            } else {
                if (!getSource().containsKey(s)) {
                    throw new UnsupportedOperationException("There is no value to return");
                }

                return getSource().get(s);
            }
        };

        if (getMapping() != null) {
            return (s, aClass) -> getterHandler.apply(getMapping().getOrDefault(s, s), aClass);
        }

        return getterHandler;
    }

    protected BiConsumer<String, Object> setterHandler() {
        return isReadOnly() ? READ_ONLY_HANDLER : (s, o) -> getSource().put(s, o);
    }

    protected BiFunction<Method, Object[], Object> methodHandler() {
        return isSupportRegularMethods() ? DEFAULT_REGULAR_METHODS_HANDLER : UNSUPPORTED_REGULAR_METHODS_HANDLER;
    }

    protected static Object defaultValue(Class<?> aClass) {
        if (aClass == void.class || !aClass.isPrimitive()) {
            return null;
        } else if (aClass == boolean.class) {
            return false;
        } else if (aClass == byte.class) {
            return (byte) 0;
        } else if (aClass == short.class) {
            return (short) 0;
        } else if (aClass == char.class) {
            return (char) 0;
        } else if (aClass == int.class) {
            return 0;
        } else if (aClass == long.class) {
            return 0L;
        } else if (aClass == float.class) {
            return 0f;
        } else if (aClass == double.class) {
            return 0.0;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    protected static final BiConsumer<String, Object> READ_ONLY_HANDLER =
            (s, o) -> { throw new UnsupportedOperationException("The bean is read only"); };

    protected static final BiFunction<Method, Object[], Object> UNSUPPORTED_REGULAR_METHODS_HANDLER =
            (method, objects) -> { throw new UnsupportedOperationException("The method " + method.getName() + " is not supported"); };

    protected static final BiFunction<Method, Object[], Object> DEFAULT_REGULAR_METHODS_HANDLER =
            (method, objects) -> defaultValue(method.getReturnType());
}

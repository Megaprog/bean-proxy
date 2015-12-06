package org.jmmo.util;

import org.junit.Test;

import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.*;

public class BeanProxyBuilderTest {

    @Test
    public void testEmptySource() throws Exception {
        Example example = BeanProxy.create(Example.class);

        assertEquals(0, example.getIntValue());
        assertNull(example.getStringValue());

        example.setIntValue(1);
        assertEquals(1, example.getIntValue());

        example.setStringValue("a");
        assertEquals("a", example.getStringValue());

        try {
            assertEquals(0L, example.getLongValue("0"));
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }

        try {
            assertEquals("false", example.setBooleanValue(false));
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testWithSource() throws Exception {
        Example example = BeanProxy.builder(Example.class).addValue("intValue", 1).setDefaultMissingValues(false).build();
        assertEquals(1, example.getIntValue());

        try {
            assertNull(example.getStringValue());
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }
    }

    @Test
    public void testTwoInterfaces() throws Exception {
        Example example = BeanProxy.builder(Example.class, ExampleTwo.class).build();
        assertEquals(0, example.getIntValue());

        ExampleTwo exampleTwo = (ExampleTwo) example;
        assertFalse(exampleTwo.isValid());

        exampleTwo.setValid(true);
        assertTrue(exampleTwo.isValid());
    }

    @Test
    public void testRegularMethods() throws Exception {
        Example example = BeanProxy.builder(Example.class).setSupportRegularMethods(true).build();
        assertEquals(0L, example.getLongValue(""));
        assertNull(example.setBooleanValue(false));
    }

    @Test
    public void testNoDefaultValues() throws Exception {
        Example example = BeanProxy.builder(Example.class).setDefaultMissingValues(false).build();

        try {
            assertEquals(0, example.getIntValue());
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }

        try {
            assertNull(example.getStringValue());
            fail();
        } catch (Exception e) {
            assertThat(e, instanceOf(UnsupportedOperationException.class));
        }

        example.setIntValue(1);
        assertEquals(1, example.getIntValue());

        example.setStringValue("a");
        assertEquals("a", example.getStringValue());
    }

    @Test
    public void testClone() throws Exception {
        BeanProxyBuilder<Example> beanProxyBuilder1 = BeanProxy.builder(Example.class).addValue("intValue", 1);
        BeanProxyBuilder<Example> beanProxyBuilder2 = beanProxyBuilder1.clone();

        Example example1 = beanProxyBuilder1.build();
        Example example2 = beanProxyBuilder2.build();

        assertEquals(1, example1.getIntValue());
        assertEquals(1, example2.getIntValue());

        example2.setIntValue(2);
        assertEquals(1, example1.getIntValue());
        assertEquals(2, example2.getIntValue());

        example1.setIntValue(3);
        assertEquals(3, example1.getIntValue());
        assertEquals(2, example2.getIntValue());
    }

    @Test
    public void testMapping() throws Exception {
        Example example = BeanProxy.builder(Example.class).addValue("value", 1).addMapping("intValue", "value").build();
        assertEquals(1, example.getIntValue());
    }

    interface Example {

        int getIntValue();

        void setIntValue(int value);

        String getStringValue();

        void setStringValue(String value);

        //not a getter has parameter
        long getLongValue(String parameter);

        //not a setter has return value
        String setBooleanValue(boolean value);
    }

    interface ExampleTwo {

        boolean isValid();

        void setValid(boolean valid);
    }
}
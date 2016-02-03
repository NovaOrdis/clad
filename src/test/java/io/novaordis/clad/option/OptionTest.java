/*
 * Copyright (c) 2016 Nova Ordis LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.novaordis.clad.option;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public abstract class OptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(OptionTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void bothLiterals() throws Exception {

        Option option = getOptionToTest('o', "option");

        assertEquals(new Character('o'), option.getShortLiteral());
        assertEquals("option", option.getLongLiteral());

        assertEquals("-o|--option", option.getLabel());
    }

    @Test
    public void shortLiteral() throws Exception {

        Option option = getOptionToTest('o', null);

        assertEquals(new Character('o'), option.getShortLiteral());
        assertNull(option.getLongLiteral());

        assertEquals("-o", option.getLabel());
    }

    @Test
    public void longLiteral() throws Exception {

        Option option = getOptionToTest(null, "option");

        assertNull(option.getShortLiteral());
        assertEquals("option", option.getLongLiteral());

        assertEquals("--option", option.getLabel());
    }

    // equals() --------------------------------------------------------------------------------------------------------

    @Test
    public void equals() throws Exception {

        Option o = getOptionToTest('t', null);
        assertEquals(o, o);
    }

    @Test
    public void equals2() throws Exception {

        Option o = getOptionToTest('t', null);
        //noinspection ObjectEqualsNull
        assertFalse(o.equals(null));
    }

    @Test
    public void equals3() throws Exception {

        Option o = getOptionToTest('t', null);

        MockOption mo = new MockOption('t');

        assertFalse(o.equals(mo));
        assertFalse(mo.equals(o));
    }

    @Test
    public void equals_SameShortLiteral_NoValue() throws Exception {

        Option o = getOptionToTest('t', null);

        assertNull(o.getValue());

        Option o2 = getOptionToTest('t', null);

        assertNull(o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_SameShortLiteral_SameValue() throws Exception {

        Option o = getOptionToTest('t', null);

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest('t', null);
        o2.setValue(value);

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_SameShortLiteral_DifferentValues() throws Exception {

        Option o = getOptionToTest('t', null);

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest('t', null);
        o2.setValue(generateDifferentValue(value));

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_SameLongLiteral_NoValue() throws Exception {

        Option o = getOptionToTest(null, "test");

        assertNull(o.getValue());

        Option o2 = getOptionToTest(null, "test");

        assertNull(o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_SameLongLiteral_SameValue() throws Exception {

        Option o = getOptionToTest(null, "test");

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest(null, "test");
        o2.setValue(value);

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_SameLongLiteral_DifferentValues() throws Exception {

        Option o = getOptionToTest(null, "test");

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest(null, "test");
        o2.setValue(generateDifferentValue(value));

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_ShortMatches_NoValue() throws Exception {

        Option o = getOptionToTest('t', "test");

        assertNull(o.getValue());

        Option o2 = getOptionToTest('t', null);

        assertNull(o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_ShortMatches_SameValue() throws Exception {

        Option o = getOptionToTest('t', "test");

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest('t', null);
        o2.setValue(value);

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_ShortMatches_DifferentValues() throws Exception {

        Option o = getOptionToTest('t', "test");

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest('t', null);
        o2.setValue(generateDifferentValue(value));

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_LongMatches_NoValue() throws Exception {

        Option o = getOptionToTest('t', "test");

        assertNull(o.getValue());

        Option o2 = getOptionToTest(null, "test");

        assertNull(o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_LongMatches_SameValue() throws Exception {

        Option o = getOptionToTest('t', "test");

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest(null, "test");
        o2.setValue(value);

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Test
    public void equals_LongMatches_DifferentValues() throws Exception {

        Option o = getOptionToTest('t', "test");

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);

        Option o2 = getOptionToTest(null, "test");
        o2.setValue(generateDifferentValue(value));

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    // setValue() ------------------------------------------------------------------------------------------------------

    @Test
    public void setValue_Null() throws Exception {

        Option o = getOptionToTest('t', null);

        o.setValue(null);
        assertNull(o.getValue());
    }

    @Test
    public void setValue() throws Exception {

        Option o = getOptionToTest('t', null);
        assertNull(o.getValue());

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);
        assertEquals(value, o.getValue());
    }

    @Test
    public void setValue_WrongType() throws Exception {

        Option o = getOptionToTest('t', null);
        assertNull(o.getValue());

        Object value = getAppropriateValueForOptionToTest();
        Object wrongType = generateDifferentType(value);

        try {
            o.setValue(wrongType);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @param shortLiteral null is acceptable (as long as longLiteral is not null)
     * @param longLiteral null is acceptable (as long as shortLiteral is not null)
     */
    protected abstract Option getOptionToTest(Character shortLiteral, String longLiteral);

    protected abstract Object getAppropriateValueForOptionToTest();

    // Private ---------------------------------------------------------------------------------------------------------

    private Object generateDifferentValue(Object value) {

        if (value instanceof String) {
            return "different " + ((String)value);
        }
        else if (value instanceof Integer) {
            return ((Integer)value) + 1;
        }
        else if (value instanceof Long) {
            return ((Long)value) + 1L;
        }
        else if (value instanceof Double) {
            return ((Double)value) + 1.0;
        }
        else if (value instanceof Boolean) {
            return !(Boolean)value;
        }
        else {

            // object, return a different object
            return new Object();
        }
    }

    private Object generateDifferentType(Object value) {

        if (value instanceof String) {
            return 1;
        }
        else if (value instanceof Integer) {
            return 1L;
        }
        else if (value instanceof Long) {
            return 1.1d;
        }
        else if (value instanceof Double) {
            return true;
        }
        else if (value instanceof Boolean) {
            return "string";
        }
        else {
            throw new RuntimeException("NOT YET IMPLEMENTED: " + value);
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class BooleanOptionTest extends OptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(BooleanOptionTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    /**
     * Booleans do not have a null default value, but 'true'
     */
    @Override
    @Test
    public void equals_SameShortLiteral_NoValue() throws Exception {

        Option o = getOptionToTest('t', null);

        assertTrue((Boolean) o.getValue());

        Option o2 = getOptionToTest('t', null);

        assertTrue((Boolean) o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    /**
     * Booleans do not have a null default value, but 'true'
     */
    @Override
    @Test
    public void equals_SameLongLiteral_NoValue() throws Exception {

        Option o = getOptionToTest(null, "test");

        assertTrue((Boolean)o.getValue());

        Option o2 = getOptionToTest(null, "test");

        assertTrue((Boolean) o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    /**
     * Booleans do not have a null default value, but 'true'
     */
    @Override
    @Test
    public void equals_ShortMatches_NoValue() throws Exception {

        Option o = getOptionToTest('t', "test");

        assertTrue((Boolean) o.getValue());

        Option o2 = getOptionToTest('t', null);

        assertTrue((Boolean) o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    /**
     * Booleans do not have a null default value, but 'true'
     */
    @Override
    @Test
    public void equals_LongMatches_NoValue() throws Exception {

        Option o = getOptionToTest('t', "test");

        assertTrue((Boolean) o.getValue());

        Option o2 = getOptionToTest(null, "test");

        assertTrue((Boolean) o2.getValue());

        assertTrue(o.equals(o2));
        assertTrue(o2.equals(o));
    }

    @Override
    @Test
    public void setValue() throws Exception {

        Option o = getOptionToTest('t', null);
        assertTrue((Boolean) o.getValue());

        Object value = getAppropriateValueForOptionToTest();
        o.setValue(value);
        assertEquals(value, o.getValue());
    }

    @Override
    @Test
    public void setValue_WrongType() throws Exception {

        Option o = getOptionToTest('t', null);
        assertTrue((Boolean) o.getValue());

        Object wrongType = "something";

        try {
            o.setValue(wrongType);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected BooleanOption getOptionToTest(Character shortLiteral, String longLiteral) {

        return new BooleanOption(shortLiteral, longLiteral);
    }

    @Override
    protected Boolean getAppropriateValueForOptionToTest() {
        return true;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

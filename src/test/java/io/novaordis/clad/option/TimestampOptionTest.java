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

import java.text.DateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TimestampOptionTest extends OptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(TimestampOptionTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // constructor -----------------------------------------------------------------------------------------------------

    @Test
    public void constructor_InvalidValue() throws Exception {

        try {

            new TimestampOption("test", "something that is not a valid timestamp");
            fail("should have thrown Exception");

        } catch (IllegalArgumentException e) {

            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("does not match neither format (full or relative)"));
        }
    }

    @Test
    public void constructor_FullValue() throws Exception {

        String timestamp = TimestampOption.DEFAULT_FULL_FORMAT.format(new Date());
        TimestampOption to = new TimestampOption("test", timestamp);
        assertEquals(timestamp, to.getValue());
        assertFalse(to.isRelative());
        assertNull(to.getShortLiteral());
        assertEquals("test", to.getLongLiteral());

    }

    @Test
    public void constructor_FullValue2() throws Exception {

        TimestampOption to = new TimestampOption('t', "test", "06/25/16 14:00:00");
        assertEquals("06/25/16 14:00:00", to.getValue());
        assertEquals(new Character('t'), to.getShortLiteral());
        assertEquals("test", to.getLongLiteral());
    }

    @Test
    public void constructor_RelativeValue() throws Exception {

        String timestamp = new TimestampOption("test", null).getRelativeFormat().format(new Date());
        TimestampOption to = new TimestampOption("test", timestamp);
        assertEquals(timestamp, to.getValue());
        assertTrue(to.isRelative());
    }

    @Test
    public void constructor_RelativeValue2() throws Exception {

        TimestampOption to = new TimestampOption('t', "test", "14:00:00");
        assertEquals("14:00:00", to.getValue());
    }

    @Test
    public void constructor_RelativeValue3() throws Exception {

        TimestampOption to = new TimestampOption('t', "test", "00:00:00");
        assertEquals("00:00:00", to.getValue());
    }

    // setValue()/getValue() -------------------------------------------------------------------------------------------

    @Test
    public void setValue_Null() throws Exception {

        TimestampOption to = new TimestampOption("test", null);
        assertNull(to.getValue());

        to.setValue(null);
        assertNull(to.getValue());
    }

    @Test
    public void setValue_NotAString() throws Exception {

        TimestampOption to = new TimestampOption("test", null);

        try {
            to.setValue(new Integer(1));
        }
        catch(IllegalArgumentException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.startsWith("value is not a String"));
        }
    }

    @Test
    public void setValue_Relative() throws Exception {

        TimestampOption to = new TimestampOption("test", null);

        to.setValue("10:00:00");
        assertEquals("10:00:00", to.getString());
        assertTrue(to.isRelative());
    }

    @Test
    public void setValue_Full() throws Exception {

        TimestampOption to = new TimestampOption("test", null);
        assertNull(to.getValue());

        to.setValue("07/11/15 10:00:00");
        assertEquals("07/11/15 10:00:00", to.getString());
        assertFalse(to.isRelative());

        to.setValue(null);
        assertNull(to.getValue());
    }

    @Test
    public void setValue_InvalidFormat() throws Exception {

        TimestampOption to = new TimestampOption("test", null);

        try {
            to.setValue("invalid format");
        }
        catch(IllegalArgumentException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("does not match neither format (full or relative)"));
        }
    }

    // getFullFormat(), getRelativeFormat() ----------------------------------------------------------------------------

    @Test
    public void formats() throws Exception {

        TimestampOption to = new TimestampOption("test", null);

        assertEquals(TimestampOption.DEFAULT_FULL_FORMAT, to.getFullFormat());
        assertNotNull(to.getRelativeFormat());
    }

    // toString() ------------------------------------------------------------------------------------------------------

    @Test
    public void toString_1() throws Exception {

        TimestampOption tso = new TimestampOption('t', "test", "15:01:02");
        assertEquals("-t|--test=15:01:02", tso.toString());
    }

    @Test
    public void toString_2() throws Exception {

        TimestampOption tso = new TimestampOption('t', "test", "07/23/16 15:01:02");
        assertEquals("-t|--test=07/23/16 15:01:02", tso.toString());
    }

    // isTimestampOptionValue() ----------------------------------------------------------------------------------------

    @Test
    public void isTimestampOptionValue_Full() throws Exception {

        boolean b = TimestampOption.isTimestampOptionValue("01/01/16 00:00:00");
        assertTrue(b);

    }
    @Test
    public void isTimestampOptionValue_Relative() throws Exception {

        boolean b = TimestampOption.isTimestampOptionValue("00:00:00");
        assertTrue(b);
    }

    @Test
    public void isTimestampOptionValue_No() throws Exception {

        boolean b = TimestampOption.isTimestampOptionValue("something");
        assertFalse(b);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TimestampOption getOptionToTest(Character shortLiteral, String longLiteral) {

        return new TimestampOption(shortLiteral, longLiteral, null);
    }

    @Override
    protected String getAppropriateValueForOptionToTest() {

        DateFormat df = new TimestampOption('t', "test", null).getFullFormat();
        return df.format(new Date());
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

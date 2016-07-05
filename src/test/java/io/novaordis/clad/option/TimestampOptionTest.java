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

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
    public void constructor_Relative() throws Exception {

        TimestampOption to = new TimestampOption('t', "test", "14:00:00");
        assertEquals(TimestampOption.DEFAULT_RELATIVE_FORMAT.parse("14:00:00"), to.getValue());
    }

    @Test
    public void constructor_Full() throws Exception {

        TimestampOption to = new TimestampOption('t', "test", "06/25/16 14:00:00");
        assertEquals(TimestampOption.DEFAULT_FULL_FORMAT.parse("06/25/16 14:00:00"), to.getValue());
    }

    @Test
    public void constructor_Failure() throws Exception {

        try {
            new TimestampOption('t', "test", "blah");
            fail("should have failed exception");
        }
        catch(ParseException e) {
            String msg = e.getMessage();
            log.info(msg);
            assertTrue(msg.contains("does not match any of the known formats"));
        }
    }

    // parseValue() ----------------------------------------------------------------------------------------------------

    @Test
    public void parseValue_NoKnownFormat() throws Exception {

        assertNull(TimestampOption.parseValue("blah"));
    }

    @Test
    public void parseValue_Relative() throws Exception {

        Date d = TimestampOption.parseValue("14:01:02");
        assertEquals(TimestampOption.DEFAULT_RELATIVE_FORMAT.parse("14:01:02"), d);
    }

    @Test
    public void parseValue_Full() throws Exception {

        Date d = TimestampOption.parseValue("07/25/16 14:01:02");
        assertEquals(TimestampOption.DEFAULT_FULL_FORMAT.parse("07/25/16 14:01:02"), d);
    }

    // getValue()/setValue() -------------------------------------------------------------------------------------------

    @Test
    public void setGetValue() throws Exception {

        TimestampOption o = getOptionToTest('t', "test");

        assertNull(o.getValue());

        String ts = "07/25/16 14:00:00";

        Date d = TimestampOption.DEFAULT_FULL_FORMAT.parse(ts);

        o.setValue(d);
        assertEquals(TimestampOption.DEFAULT_FULL_FORMAT.parse(ts), o.getValue());

        assertEquals("07/25/16 14:00:00", o.getString());
    }

    @Test
    public void relative() throws Exception {

        TimestampOption o = new TimestampOption('t', "test", "14:00:00");
        assertTrue(o.isRelative());
    }

    @Test
    public void absolute() throws Exception {

        TimestampOption o = new TimestampOption('t', "test", "07/25/16 14:00:00");
        assertFalse(o.isRelative());
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

    @Test
    public void toString_3() throws Exception {

        TimestampOption tso = new TimestampOption('t', "test");
        assertEquals("-t|--test=", tso.toString());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected TimestampOption getOptionToTest(Character shortLiteral, String longLiteral) {

        return new TimestampOption(shortLiteral, longLiteral);
    }

    @Override
    protected Date getAppropriateValueForOptionToTest() {

        try {
            return TimestampOption.DEFAULT_FULL_FORMAT.parse("07/25/16 14:00:00");
        }
        catch (ParseException e) {

            throw new IllegalStateException(e);
        }
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TimestampOptionTest extends OptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void setGetValue() throws Exception {

        TimestampOption o = getOptionToTest('t', "test");

        assertNull(o.getValue());

        String ts = "07/25/16 14:00:00";

        Date d = TimestampOption.DEFAULT_FORMAT.parse(ts);

        o.setValue(d);
        assertEquals(TimestampOption.DEFAULT_FORMAT.parse(ts), o.getValue());

        assertEquals("07/25/16 14:00:00", o.getString());
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
            return TimestampOption.DEFAULT_FORMAT.parse("07/25/16 14:00:00");
        }
        catch (ParseException e) {

            throw new IllegalStateException(e);
        }
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

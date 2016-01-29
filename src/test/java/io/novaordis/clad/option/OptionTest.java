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

import io.novaordis.clad.option.Option;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public abstract class OptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void bothLiterals() throws Exception {

        Option option = getOptionToTest('o', "option");

        assertEquals(new Character('o'), option.getShortLiteral());
        assertEquals("option", option.getLongLiteral());
    }

    @Test
    public void shortLiteral() throws Exception {

        Option option = getOptionToTest('o', null);

        assertEquals(new Character('o'), option.getShortLiteral());
        assertNull(option.getLongLiteral());
    }

    @Test
    public void longLiteral() throws Exception {

        Option option = getOptionToTest(null, "option");

        assertNull(option.getShortLiteral());
        assertEquals("option", option.getLongLiteral());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    /**
     * @param shortLiteral null is acceptable (as long as longLiteral is not null)
     * @param longLiteral null is acceptable (as long as shortLiteral is not null)
     */
    protected abstract Option getOptionToTest(Character shortLiteral, String longLiteral);

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class VerboseOptionTest extends BooleanOptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void bothLiterals() throws Exception {

        VerboseOption verboseOption = getOptionToTest(null, null);
        assertEquals(VerboseOption.SHORT_LITERAL, verboseOption.getShortLiteral());
        assertEquals(VerboseOption.LONG_LITERAL, verboseOption.getLongLiteral());
    }

    @Test
    public void shortLiteral() throws Exception {

        VerboseOption verboseOption = getOptionToTest(null, null);
        assertEquals(VerboseOption.SHORT_LITERAL, verboseOption.getShortLiteral());
    }

    @Test
    public void longLiteral() throws Exception {

        VerboseOption verboseOption = getOptionToTest(null, null);
        assertEquals(VerboseOption.LONG_LITERAL, verboseOption.getLongLiteral());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected VerboseOption getOptionToTest(Character shortLiteral, String longLiteral) {

        return new VerboseOption();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

package io.novaordis.clad.variable;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public class ConstantTest extends TokenTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void resolve() throws Exception {

        Constant c = new Constant("something");
        String s2 = c.resolve((VariableProvider)null);

        assertEquals("something", s2);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected Constant getTokenToTest() throws Exception {

        return new Constant("something");
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

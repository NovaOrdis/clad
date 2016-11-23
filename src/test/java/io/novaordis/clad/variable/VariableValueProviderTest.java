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
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public abstract class VariableValueProviderTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void setVariable() throws Exception {

        VariableProvider r = getVariableValueProviderToTest();

        assertNull(r.getValue("something"));
        assertNull(r.setValue("something", "A"));
        assertEquals("A", r.getValue("something"));
        assertEquals("A", r.setValue("something", "B"));
        assertEquals("B", r.getValue("something"));
        assertEquals("B", r.setValue("something", null));
        assertNull(r.getValue("something"));
        assertNull(r.setValue("something", "C"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract VariableProvider getVariableValueProviderToTest() throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

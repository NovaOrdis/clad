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

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public abstract class TokenTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void getLiteral() throws Exception {

        Token t = getTokenToTest();

        String literal = t.getLiteral();

        assertNotNull(literal);

        if (t instanceof Constant) {

            assertFalse(literal.contains("${"));
        }
        else if (t instanceof Variable) {

            assertTrue(literal.matches("\\$\\{.*\\}"));
        }
        else {
            fail("unknown token type " + t);
        }
    }

    @Test
    public void resolve_NullProvider() throws Exception {

        Token t = getTokenToTest();

        String v = t.resolve((VariableProvider) null);
        assertEquals(t.getLiteral(), v);
    }

    @Test
    public void resolve_NullMap() throws Exception {

        Token t = getTokenToTest();

        String v = t.resolve((Map<String, String>)null);
        assertEquals(t.getLiteral(), v);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract Token getTokenToTest() throws Exception;

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

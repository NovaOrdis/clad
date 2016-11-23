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

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public class VariableTest extends TokenTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void resolve_NullProvider() throws Exception {

        Variable v = new Variable("something");

        String s = v.resolve((VariableProvider)null);

        assertEquals("${something}", s);
    }

    @Test
    public void resolve() throws Exception {

        Variable v = new Variable("something");

        VariableProviderImpl p = new VariableProviderImpl();

        String s = v.resolve(p);

        assertEquals("${something}", s);

        p.setValue("something", "blah");

        assertEquals("blah", v.resolve(p));
    }

    @Test
    public void resolve_Map_KeyExists() throws Exception {

        Variable v = new Variable("something");

        Map<String, String> map = new HashMap<>();
        map.put("something", "blah");

        String s = v.resolve(map);
        assertEquals("blah", s);
    }

    @Test
    public void resolve_Map_KeyDoesNotExist() throws Exception {

        Variable v = new Variable("something");

        Map<String, String> map = new HashMap<>();
        map.put("somethingelse", "blah");

        String s = v.resolve(map);
        assertEquals("${something}", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    @Override
    protected Variable getTokenToTest() throws Exception {

        return new Variable("test");
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

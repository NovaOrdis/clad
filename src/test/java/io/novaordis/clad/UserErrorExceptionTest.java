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

package io.novaordis.clad;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/21/16
 */
public class UserErrorExceptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void constructor() throws Exception {

        try {
            throw new UserErrorException();
        }
        catch (UserErrorException e) {
            assertNull(e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void constructor2() throws Exception {

        try {
            throw new UserErrorException("test");
        }
        catch (UserErrorException e) {
            assertEquals("test", e.getMessage());
            assertNull(e.getCause());
        }
    }

    @Test
    public void constructor3() throws Exception {

        try {
            throw new UserErrorException(new RuntimeException());
        }
        catch (UserErrorException e) {
            assertNull(e.getMessage());
            assertTrue(e.getCause() instanceof RuntimeException);
        }
    }

    @Test
    public void constructor4() throws Exception {

        try {
            throw new UserErrorException("test", new RuntimeException());
        }
        catch (UserErrorException e) {
            assertEquals("test", e.getMessage());
            assertTrue(e.getCause() instanceof RuntimeException);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

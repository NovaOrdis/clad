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

package io.novaordis.clad.command;

import io.novaordis.clad.Command;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/29/16
 */
public abstract class CommandTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // getName() -------------------------------------------------------------------------------------------------------

    @Test
    public void getName() throws Exception {

        Command c = getCommandToTest();
        String name = c.getName();
        assertNotNull(name);

        String s = c.getClass().getSimpleName();
        s = s.replaceAll("Command", "").toLowerCase();
        assertEquals(s, name);
    }

    // getHelpFilePath() -----------------------------------------------------------------------------------------------

    @Test
    public void getHelpFilePath() throws Exception {

        Command c = getCommandToTest();
        String helpFilePath = c.getHelpFilePath();
        assertNotNull(helpFilePath);

        String s = c.getClass().getName();
        s = s.substring(0, s.lastIndexOf('.'));
        s = s.replace('.', '/');
        s = s + "/" + c.getName() + ".txt";
        assertEquals(s, helpFilePath);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Command getCommandToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

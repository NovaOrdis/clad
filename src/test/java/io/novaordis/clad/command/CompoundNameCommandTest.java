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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/27/16
 */
public class CompoundNameCommandTest extends CommandTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(VersionCommand.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // getName() -------------------------------------------------------------------------------------------------------

    @Test
    @Override
    public void getName() throws Exception {

        //
        // command name should contain dashes
        //

        Command c = getCommandToTest();
        String name = c.getName();
        assertEquals("compound-name", name);
    }

    // getHelpFilePath() -----------------------------------------------------------------------------------------------

    @Test
    @Override
    public void getHelpFilePath() throws Exception {

        //
        // in-line help file name should contain dashes
        //

        Command c = getCommandToTest();
        String helpFilePath = c.getHelpFilePath();
        log.info(helpFilePath);

        String s = c.getClass().getName();
        s = s.substring(0, s.lastIndexOf('.'));
        s = s.replace('.', '/');
        s = s + "/compound-name.txt";
        assertEquals(s, helpFilePath);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected CompoundNameCommand getCommandToTest() throws Exception {
        return new CompoundNameCommand();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class CommandLineApplicationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CommandLineApplicationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void identifyCommand_NoArguments() throws Exception {

        List<String> commandLineArguments = new ArrayList<>();
        Command c = CommandLineApplication.identifyCommand(commandLineArguments);

        assertNull(c);
        assertTrue(commandLineArguments.isEmpty());
    }

    @Test
    public void identifyCommand_NoArguments_NoSuchCommand() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Collections.singletonList("no-such-command"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments);

        assertNull(c);

        assertEquals(1, commandLineArguments.size());
        assertEquals("no-such-command", commandLineArguments.get(0));
    }

    @Test
    public void identifyCommand_NoArguments_TWwoNoSuchCommand() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList("no-such-command", "no-such-command-2"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments);

        assertNull(c);

        assertEquals(2, commandLineArguments.size());
        assertEquals("no-such-command", commandLineArguments.get(0));
        assertEquals("no-such-command-2", commandLineArguments.get(1));
    }

    @Test
    public void identifyCommand_testCommand_NoArguments() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Collections.singletonList("test"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);
        assertEquals(0, testCommand.getCommandLineArguments().size());

        // make sure the command name disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    @Test
    public void identifyCommand_testCommand_OneArgument() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList("test", "test-command-arg-0"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);
        assertEquals(1, testCommand.getCommandLineArguments().size());
        assertEquals("test-command-arg-0", testCommand.getCommandLineArguments().get(0));

        // make sure the command name disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    @Test
    public void identifyCommand_testCommand_TwoArguments() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList(
                "test", "test-command-arg-0", "test-command-arg-1"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);
        assertEquals(2, testCommand.getCommandLineArguments().size());
        assertEquals("test-command-arg-0", testCommand.getCommandLineArguments().get(0));
        assertEquals("test-command-arg-1", testCommand.getCommandLineArguments().get(1));

        // make sure the command name disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    // getFileNames() --------------------------------------------------------------------------------------------------

    @Test
    public void getFileNames() throws Exception {

        String basedir = System.getProperty("basedir");
        assertNotNull(basedir);

        File data = new File(basedir, "src/test/resources/data");
        assertTrue(data.isDirectory());

        List<String> names = CommandLineApplication.getFileNames(data.getPath(), data);
        assertEquals(2, names.size());

        String name = data.getPath() + File.separator + "testDir" + File.separator  + "A.txt";
        assertEquals(name, names.get(0));
        name = data.getPath() + File.separator + "testDir" + File.separator  + "testSubDir" +  File.separator  + "B.txt";
        assertEquals(name, names.get(1));
    }

    // commandNameToSimpleClassName() ----------------------------------------------------------------------------------

    @Test
    public void commandNameToSimpleClassName_Null() throws Exception {

        try {
            CommandLineApplication.commandNameToSimpleClassName(null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            log.debug(e.getMessage());
        }
    }

    @Test
    public void commandNameToSimpleClassName() throws Exception {

        String s = CommandLineApplication.commandNameToSimpleClassName("test");
        assertEquals("TestCommand", s);
    }

    @Test
    public void commandNameToSimpleClassName2() throws Exception {

        String s = CommandLineApplication.commandNameToSimpleClassName("Test");
        assertEquals("TestCommand", s);
    }

    @Test
    public void commandNameToSimpleClassName3() throws Exception {

        String s = CommandLineApplication.commandNameToSimpleClassName("TEST");
        assertEquals("TestCommand", s);
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

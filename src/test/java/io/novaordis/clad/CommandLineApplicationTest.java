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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

    @Before
    public void setUp() {
        System.setProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME, "test");
    }

    @After
    public void tearDown() {
        System.clearProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME);
    }

    @Test
    public void identifyCommand_NoArguments() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        List<String> commandLineArguments = new ArrayList<>();
        Command c = CommandLineApplication.identifyCommand(commandLineArguments, configuration);

        assertNull(c);
        assertTrue(commandLineArguments.isEmpty());
    }

    @Test
    public void identifyCommand_NoArguments_NoSuchCommand() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        List<String> commandLineArguments = new ArrayList<>(Collections.singletonList("no-such-command"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments, configuration);

        assertNull(c);

        assertEquals(1, commandLineArguments.size());
        assertEquals("no-such-command", commandLineArguments.get(0));
    }

    @Test
    public void identifyCommand_NoArguments_TWwoNoSuchCommand() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        List<String> commandLineArguments = new ArrayList<>(Arrays.asList("no-such-command", "no-such-command-2"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments, configuration);

        assertNull(c);

        assertEquals(2, commandLineArguments.size());
        assertEquals("no-such-command", commandLineArguments.get(0));
        assertEquals("no-such-command-2", commandLineArguments.get(1));
    }

    @Test
    public void identifyCommand_testCommand_NoArguments() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        List<String> commandLineArguments = new ArrayList<>(Collections.singletonList("test"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments, configuration);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);

        assertEquals(0, configuration.getCommandOptions().size());

        // make sure the command name disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    @Test
    public void identifyCommand_testCommand_OneArgument() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        List<String> commandLineArguments = new ArrayList<>(Arrays.asList("test", "-m"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments, configuration);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);
        assertEquals(1, configuration.getCommandOptions().size());
        BooleanOption bo = (BooleanOption)configuration.getCommandOptions().get(0);
        assertEquals('m', bo.getShortLiteral().charValue());
        assertNull(bo.getLongLiteral());
        assertTrue(bo.getValue());

        // make sure the command name and its arguments disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    @Test
    public void identifyCommand_testCommand_TwoArguments() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        List<String> commandLineArguments = new ArrayList<>(Arrays.asList("test", "-n", "test"));

        Command c = CommandLineApplication.identifyCommand(commandLineArguments, configuration);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);
        assertEquals(1, configuration.getCommandOptions().size());

        StringOption so = (StringOption)configuration.getCommandOptions().get(0);
        assertEquals('n', so.getShortLiteral().charValue());
        assertEquals("test", so.getValue());

        // make sure the command name disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    // identifyRuntime() -----------------------------------------------------------------------------------------------

    @Test
    public void identifyRuntime_ConfigurationDoesNotHaveTheApplicationName() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.setApplicationName(null);

        try {
            CommandLineApplication.identifyRuntime(configuration);
            fail("should have thrown exception, missing application name");
        }
        catch(UserErrorException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void identifyRuntime() throws Exception {

        ConfigurationImpl configuration = new ConfigurationImpl();
        configuration.setApplicationName("test");

        TestApplicationRuntime runtime = (TestApplicationRuntime)CommandLineApplication.identifyRuntime(configuration);
        assertNotNull(runtime);
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

    // toSimpleClassName() ---------------------------------------------------------------------------------------------

    @Test
    public void toSimpleClassName_Null() throws Exception {

        try {
            CommandLineApplication.toSimpleClassName(null, "Something");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            log.debug(e.getMessage());
        }

        try {
            CommandLineApplication.toSimpleClassName("something", null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            log.debug(e.getMessage());
        }
    }

    @Test
    public void toSimpleClassName() throws Exception {

        String s = CommandLineApplication.toSimpleClassName("test", "Command");
        assertEquals("TestCommand", s);
    }

    @Test
    public void toSimpleClassName2() throws Exception {

        String s = CommandLineApplication.toSimpleClassName("Test", "Command");
        assertEquals("TestCommand", s);
    }

    @Test
    public void toSimpleClassName3() throws Exception {

        String s = CommandLineApplication.toSimpleClassName("TEST", "Command");
        assertEquals("TestCommand", s);
    }

    @Test
    public void toSimpleClassName4() throws Exception {

        String s = CommandLineApplication.toSimpleClassName("test", "ApplicationRuntime");
        assertEquals("TestApplicationRuntime", s);
    }

    @Test
    public void toSimpleClassName5() throws Exception {

        String s = CommandLineApplication.toSimpleClassName("TEST", "ApplicationRuntime");
        assertEquals("TestApplicationRuntime", s);
    }

    // main() ----------------------------------------------------------------------------------------------------------


    @Test
    public void main() throws Exception {

        try {

            assertFalse(TestApplicationRuntime.isInitialized());
            assertTrue(TestCommand.getGlobalOptionsInjectedByExecution().isEmpty());
            assertTrue(TestCommand.getCommandOptionsInjectedByExecution().isEmpty());

            String[] args = new String[]
                    {
                            "-g", "global-value", "--global2=global2-value",
                            "test",
                            "-c", "command-value", "--command2=command2-value"
                    };


            CommandLineApplication.main(args);

            assertTrue(TestApplicationRuntime.isInitialized());

            List<Option> globalOptions = TestCommand.getGlobalOptionsInjectedByExecution();

            assertEquals(2, globalOptions.size());

            StringOption option = (StringOption)globalOptions.get(0);
            assertEquals('g', option.getShortLiteral().charValue());
            assertEquals("global-value", option.getValue());

            option = (StringOption)globalOptions.get(1);
            assertNull(option.getShortLiteral());
            assertEquals("global2", option.getLongLiteral());
            assertEquals("global2-value", option.getValue());

            List<Option> commandOptions = TestCommand.getCommandOptionsInjectedByExecution();

            assertEquals(2, commandOptions.size());

            option = (StringOption)commandOptions.get(0);
            assertEquals('c', option.getShortLiteral().charValue());
            assertEquals("command-value", option.getValue());

            option = (StringOption)commandOptions.get(1);
            assertNull(option.getShortLiteral());
            assertEquals("command2", option.getLongLiteral());
            assertEquals("command2-value", option.getValue());
        }
        finally {

            TestCommand.clear();
            assertTrue(TestCommand.getGlobalOptionsInjectedByExecution().isEmpty());
            assertTrue(TestCommand.getCommandOptionsInjectedByExecution().isEmpty());
            TestApplicationRuntime.clear();
            assertFalse(TestApplicationRuntime.isInitialized());
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

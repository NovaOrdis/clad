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

import io.novaordis.clad.option.BooleanOption;
import io.novaordis.clad.option.Option;
import io.novaordis.clad.option.StringOption;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    // execute() -------------------------------------------------------------------------------------------------------

    @Test
    public void execute() throws Exception {

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


            int exitCode = new CommandLineApplication().execute(args);

            assertEquals(0, exitCode);

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

    @Test
    public void main_NoDefaultCommand() throws Exception {

        try {

            assertFalse(TestApplicationRuntime.isInitialized());

            CommandLineApplication commandLineApplication = new CommandLineApplication();

            MockOutputStream mos = new MockOutputStream();

            commandLineApplication.setStderrOutputStream(mos);

            String[] args = new String[] {"-g", "global-value"};

            int exitCode = commandLineApplication.execute(args);

            assertEquals(1, exitCode);

            byte[] bytes = mos.getWrittenBytes();
            String msg = new String(bytes);
            assertEquals("[error]: no command specified on command line and no default command was configured\n", msg);

            assertFalse(TestApplicationRuntime.isInitialized());
        }
        finally {
            TestApplicationRuntime.clear();
            assertFalse(TestApplicationRuntime.isInitialized());
        }
    }

    @Test
    public void main_UnknownCommand() throws Exception {

        try {

            assertFalse(TestApplicationRuntime.isInitialized());

            CommandLineApplication commandLineApplication = new CommandLineApplication();

            MockOutputStream mos = new MockOutputStream();

            commandLineApplication.setStderrOutputStream(mos);

            String[] args = new String[] {"something"};

            int exitCode = commandLineApplication.execute(args);

            assertEquals(1, exitCode);

            byte[] bytes = mos.getWrittenBytes();
            String msg = new String(bytes);
            assertEquals("[error]: unknown option: 'something'\n", msg);

            assertFalse(TestApplicationRuntime.isInitialized());
        }
        finally {
            TestApplicationRuntime.clear();
            assertFalse(TestApplicationRuntime.isInitialized());
        }
    }

    // help() ----------------------------------------------------------------------------------------------------------

    @Test
    public void help() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"help", "test"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void help2() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"--help", "test"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void help3() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"--help=test"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void help4() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"-h", "test"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void unknownCommandHelp() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"help", "no-such-command"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(1, exitCode);
        String msg = mos.getWrittenString();
        assertEquals("[error]: unknown command: 'no-such-command'\n", msg);
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void unknownCommandHelp2() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"--help", "no-such-command"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(1, exitCode);
        assertEquals("[error]: unknown command: 'no-such-command'\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void unknownCommandHelp3() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"--help=no-such-command"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(1, exitCode);
        assertEquals("[error]: unknown command: 'no-such-command'\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void unknownCommandHelp4() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"-h", "no-such-command"};

        int exitCode = commandLineApplication.execute(args);

        assertEquals(1, exitCode);
        assertEquals("[error]: unknown command: 'no-such-command'\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    // streams ---------------------------------------------------------------------------------------------------------

    @Test
    public void streams() throws Exception {

        CommandLineApplication a = new CommandLineApplication();

        assertEquals(System.out, a.getStdoutOutputStream());
        assertEquals(System.err, a.getStderrOutputStream());

        MockOutputStream mos = new MockOutputStream();

        a.setStderrOutputStream(mos);
        assertEquals(mos, a.getStderrOutputStream());

        a.setStdoutOutputStream(mos);
        assertEquals(mos, a.getStdoutOutputStream());

    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

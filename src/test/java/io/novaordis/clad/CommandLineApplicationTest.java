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

import io.novaordis.clad.application.SyntheticException;
import io.novaordis.clad.application.TestApplicationRuntime;
import io.novaordis.clad.command.Command;
import io.novaordis.clad.command.TestCommand;
import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.configuration.ConfigurationImpl;
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

        TestCommand.clear();
        TestApplicationRuntime.clear();
        System.clearProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME);
        System.clearProperty("DoesNotNeedRuntimeCommand.executed");
    }

    @Test
    public void identifyAndConfigureCommand_NoArguments() throws Exception {

        List<String> commandLineArguments = new ArrayList<>();
        Command c = CommandLineApplication.identifyAndConfigureCommand(commandLineArguments);

        assertNull(c);
        assertTrue(commandLineArguments.isEmpty());
    }

    @Test
    public void identifyAndConfigureCommand_NoArguments_NoSuchCommand() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Collections.singletonList("no-such-command"));

        Command c = CommandLineApplication.identifyAndConfigureCommand(commandLineArguments);

        assertNull(c);

        assertEquals(1, commandLineArguments.size());
        assertEquals("no-such-command", commandLineArguments.get(0));
    }

    @Test
    public void identifyAndConfigureCommand_TwoArguments_NoSuchCommand() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList("no-such-command", "no-such-command-2"));

        Command c = CommandLineApplication.identifyAndConfigureCommand(commandLineArguments);

        assertNull(c);

        assertEquals(2, commandLineArguments.size());
        assertEquals("no-such-command", commandLineArguments.get(0));
        assertEquals("no-such-command-2", commandLineArguments.get(1));
    }

    @Test
    public void identifyAndConfigureCommand_testCommand_NoArguments() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Collections.singletonList("test"));

        Command c = CommandLineApplication.identifyAndConfigureCommand(commandLineArguments);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);

        assertEquals(0, testCommand.getOptions().size());

        // make sure the command name disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    @Test
    public void identifyAndConfigureCommand_testCommand_OneKnownArgument() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList(
                "test", "--required-test-command-option=test-value"));

        Command c = CommandLineApplication.identifyAndConfigureCommand(commandLineArguments);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);
        assertEquals(1, testCommand.getOptions().size());
        StringOption so = (StringOption)testCommand.getOptions().get(0);
        assertEquals("required-test-command-option", so.getLongLiteral());
        assertNull(so.getShortLiteral());
        assertEquals("test-value", so.getString());

        // make sure the command name and its arguments disappeared from the argument list
        assertEquals(0, commandLineArguments.size());
    }

    @Test
    public void identifyAndConfigureCommand_testCommand_OneKnownArgumentAndOneUnknownArgument() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList(
                "test", "-x", "--required-test-command-option=test-value"));

        Command c = CommandLineApplication.identifyAndConfigureCommand(commandLineArguments);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);
        assertEquals(1, testCommand.getOptions().size());
        StringOption so = (StringOption)testCommand.getOptions().get(0);
        assertEquals("required-test-command-option", so.getLongLiteral());
        assertNull(so.getShortLiteral());
        assertEquals("test-value", so.getString());

        // make sure the command name and its arguments disappeared from the argument list
        assertEquals(1, commandLineArguments.size());
        assertEquals("-x", commandLineArguments.get(0));
    }

    @Test
    public void identifyAndConfigureCommand_testCommand_TwoKnownArguments_TwoUnknownArguments() throws Exception {

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList(
                "test", "--required-test-command-option=test-value", "-x", "blah", "-t", "test-value-2", "-y"));

        Command c = CommandLineApplication.identifyAndConfigureCommand(commandLineArguments);

        TestCommand testCommand = (TestCommand)c;
        assertNotNull(testCommand);

        assertEquals(2, testCommand.getOptions().size());

        StringOption so = (StringOption)testCommand.getOptions().get(0);
        assertEquals("required-test-command-option", so.getLongLiteral());
        assertNull(so.getShortLiteral());
        assertEquals("test-value", so.getString());

        StringOption so2 = (StringOption)testCommand.getOptions().get(1);
        assertEquals('t', so2.getShortLiteral().charValue());
        assertEquals("test-value-2", so2.getValue());

        // make sure the command name disappeared from the argument list
        assertEquals(3, commandLineArguments.size());
        assertEquals("-x", commandLineArguments.get(0));
        assertEquals("blah", commandLineArguments.get(1));
        assertEquals("-y", commandLineArguments.get(2));
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
    public void unknownCommandLineArgument() throws Exception {

        assertFalse(TestApplicationRuntime.isInitialized());
        assertTrue(TestCommand.getGlobalOptionsInjectedByExecution().isEmpty());
        TestApplicationRuntime.addOptionalGlobalOption(new StringOption('g'));
        TestApplicationRuntime.addOptionalGlobalOption(new StringOption("global2"));

        String[] args = new String[]
                {
                        "-g", "global-value", "--global2=global2-value",
                        "test",
                        "--required-test-command-option=test-command-value",
                        "bananas",
                        "-t", "test-command-value-2"
                };

        MockOutputStream mos = new MockOutputStream();

        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        int exitCode = commandLineApplication.run(args);

        assertEquals(1, exitCode);

        assertFalse(TestApplicationRuntime.isInitialized());

        String s = mos.getWrittenString();
        log.info(s);
        assertEquals("[error]: unknown command(s) or option(s): bananas\n", s);
    }

    @Test
    public void execute() throws Exception {

        try {

            assertFalse(TestApplicationRuntime.isInitialized());
            assertTrue(TestCommand.getGlobalOptionsInjectedByExecution().isEmpty());
            TestApplicationRuntime.addOptionalGlobalOption(new StringOption('g'));
            TestApplicationRuntime.addOptionalGlobalOption(new StringOption("global2"));

            String[] args = new String[]
                    {
                            "-g", "global-value", "--global2=global2-value",
                            "test",
                            "--required-test-command-option=test-command-value",
                            "-t", "test-command-value-2"
                    };

            CommandLineApplication commandLineApplication = new CommandLineApplication();

            int exitCode = commandLineApplication.run(args);

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

            TestCommand testCommand = (TestCommand)commandLineApplication.getCommand();

            List<Option> commandOptions = testCommand.getOptions();

            assertEquals(2, commandOptions.size());

            option = (StringOption)commandOptions.get(0);
            assertNull(option.getShortLiteral());
            assertEquals("required-test-command-option", option.getLongLiteral());
            assertEquals("test-command-value", option.getValue());

            option = (StringOption)commandOptions.get(1);
            assertEquals('t', option.getShortLiteral().charValue());
            assertEquals("test-command-value-2", option.getValue());
        }
        finally {

            TestCommand.clear();
            assertTrue(TestCommand.getGlobalOptionsInjectedByExecution().isEmpty());
            TestApplicationRuntime.clear();
            assertFalse(TestApplicationRuntime.isInitialized());
        }
    }

    @Test
    public void main_NoDefaultCommand() throws Exception {

        assertFalse(TestApplicationRuntime.isInitialized());

        CommandLineApplication commandLineApplication = new CommandLineApplication();

        MockOutputStream mos = new MockOutputStream();

        commandLineApplication.setStderrOutputStream(mos);

        String[] args = new String[] {"-g", "global-value"};

        TestApplicationRuntime.addOptionalGlobalOption(new StringOption('g'));

        int exitCode = commandLineApplication.run(args);

        assertEquals(1, exitCode);

        byte[] bytes = mos.getWrittenBytes();
        String msg = new String(bytes);
        assertEquals("[error]: no command specified on command line and no default command was configured\n", msg);

        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void main_UnknownCommand() throws Exception {

        try {

            assertFalse(TestApplicationRuntime.isInitialized());

            CommandLineApplication commandLineApplication = new CommandLineApplication();

            MockOutputStream mos = new MockOutputStream();

            commandLineApplication.setStderrOutputStream(mos);

            String[] args = new String[] {"something"};

            int exitCode = commandLineApplication.run(args);

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

    // help() ----------------------------------------------------------------------------------------------------------

    @Test
    public void help() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"help", "test"};

        int exitCode = commandLineApplication.run(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void help_AllCommands() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] { "help" };

        //
        // all commands help
        //

        int exitCode = commandLineApplication.run(args);

        assertEquals(0, exitCode);

        String allCommandsHelp = mos.getWrittenString();

        assertTrue(allCommandsHelp.contains("test"));
        assertTrue(allCommandsHelp.contains("test2"));
        assertTrue(allCommandsHelp.contains("version"));

        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void help2() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"--help", "test"};

        int exitCode = commandLineApplication.run(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void help3() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"--help=test"};

        int exitCode = commandLineApplication.run(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void help4() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos, null);

        String[] args = new String[] {"-h", "test"};

        int exitCode = commandLineApplication.run(args);

        assertEquals(0, exitCode);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void unknownCommandHelp() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"help", "no-such-command"};

        int exitCode = commandLineApplication.run(args);

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

        int exitCode = commandLineApplication.run(args);

        assertEquals(1, exitCode);
        assertEquals("[error]: unknown command: 'no-such-command'\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void unknownCommandHelp3() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"--help=no-such-command"};

        int exitCode = commandLineApplication.run(args);

        assertEquals(1, exitCode);
        assertEquals("[error]: unknown command: 'no-such-command'\n", mos.getWrittenString());
        assertFalse(TestApplicationRuntime.isInitialized());
    }

    @Test
    public void unknownCommandHelp4() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"-h", "no-such-command"};

        int exitCode = commandLineApplication.run(args);

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

    // run() -----------------------------------------------------------------------------------------------------------

    @Test
    public void run_RuntimeThrowsExceptionOnInitialization_CommandNeedsRuntime() throws Exception {

        //
        // force an application runtime that throws exception on initialization
        //

        System.setProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME, "exception-on-initialization");

        CommandLineApplication cla = new CommandLineApplication();

        try {
            cla.run(new String[]{"needs-runtime"});
            fail("should have thrown exception because the runtime initialization fails");
        }
        catch(SyntheticException e) {
            String msg = e.getMessage();
            assertEquals("SYNTHETIC", msg);
        }
    }

    @Test
    public void run_RuntimeThrowsExceptionOnInitialization_CommandDoesNotNeedRuntime() throws Exception {

        //
        // force an application runtime that throws exception on initialization
        //

        System.setProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME, "exception-on-initialization");

        CommandLineApplication cla = new CommandLineApplication();

        cla.run(new String[] {"does-not-need-runtime"});

        //
        // if we get here we're fine
        //

        assertEquals("true", System.getProperty("DoesNotNeedRuntimeCommand.executed"));
    }

    @Test
    public void run_RequiredCommandOptionMissing() throws Exception {

        MockOutputStream mos = new MockOutputStream();
        CommandLineApplication commandLineApplication = new CommandLineApplication(mos);

        String[] args = new String[] {"test"};

        int exitCode = commandLineApplication.run(args);

        assertEquals(1, exitCode);
        String s = mos.getWrittenString();
        assertEquals("[error]: required \"test\" command option \"--required-test-command-option\" is missing\n", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

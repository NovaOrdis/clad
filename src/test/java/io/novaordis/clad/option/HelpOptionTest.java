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

package io.novaordis.clad.option;

import io.novaordis.clad.application.TestApplicationRuntime;
import io.novaordis.clad.command.Command;
import io.novaordis.clad.MockOutputStream;
import io.novaordis.clad.command.Test2Command;
import io.novaordis.clad.command.TestCommand;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class HelpOptionTest extends OptionTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(HelpOptionTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Overrides -------------------------------------------------------------------------------------------------------

    @Override
    @Test
    public void setValue_WrongType() throws Exception {

        // noop - there are no wrong values for Help option
    }

    @Override
    @Test
    public void setValue() throws Exception {

        // noop - setValue() means nothing to HelpOption
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void bothLiterals() throws Exception {

        Option option = getOptionToTest(null, null);

        assertEquals(HelpOption.SHORT_LITERAL, option.getShortLiteral());
        assertEquals(HelpOption.LONG_LITERAL, option.getLongLiteral());
    }

    @Test
    public void shortLiteral() throws Exception {
        // noop
    }

    @Test
    public void longLiteral() throws Exception {
        // noop
    }

    @Test
    public void displayHelp_CommandExists_HelpFileExists() throws Exception {

        MockOutputStream mos = new MockOutputStream();

        HelpOption helpOption = new HelpOption();
        TestCommand command = new TestCommand();
        helpOption.setCommand(command);

        helpOption.displayHelp(mos);

        assertEquals("test help line 1\ntest help line 2\n", mos.getWrittenString());
    }

    @Test
    public void displayHelp_CommandExists_HelpFileDoesNotExist() throws Exception {

        MockOutputStream mos = new MockOutputStream();

        HelpOption helpOption = new HelpOption();
        Test2Command command = new Test2Command();
        helpOption.setCommand(command);

        helpOption.displayHelp(mos);

        String s = mos.getWrittenString();
        assertEquals(HelpOption.NO_COMMAND_HELP_FOUND_TEXT + " 'test2'\n", s);
    }

    @Test
    public void displayHelp_GenericApplicationHelpAndMacros() throws Exception {

        HelpOption helpOption = new HelpOption();
        assertNull(helpOption.getCommand());
        assertNull(helpOption.getValue());
        helpOption.setApplication(new TestApplicationRuntime());

        MockOutputStream mos = new MockOutputStream();

        helpOption.displayHelp(mos);

        String s = mos.getWrittenString();
        assertTrue(s.contains("this is application help placeholder"));
    }

    // setCommand() ----------------------------------------------------------------------------------------------------

    @Test
    public void setCommand() throws Exception {

        HelpOption helpOption = new HelpOption();

        assertNull(helpOption.getCommand());

        Command c = new TestCommand();

        helpOption.setCommand(c);
        assertEquals(c, helpOption.getCommand());

        assertEquals("test", helpOption.getValue());
    }

    @Test
    public void setCommand_CommandAlreadySet() throws Exception {

        HelpOption helpOption = new HelpOption();
        helpOption.setCommand(new TestCommand());

        try {
            helpOption.setCommand(new Test2Command());
            fail("should throw exception");
        }
        catch(IllegalStateException e) {
            log.info(e.getMessage());
        }

        assertTrue(helpOption.getCommand() instanceof TestCommand);
    }

    // getHelpContent() ------------------------------------------------------------------------------------------------

    @Test
    public void getHelpContent() throws Exception {
        String file = "data/help.txt"; // this is in the test classpath
        byte[] result = HelpOption.getHelpContent(file);
        assertEquals("test help\n", new String(result));
    }

    @Test
    public void getHelpContent_NoNewLineAtTheEndOfStorage() throws Exception {
        String file = "data/help-no-newline.txt"; // this is in the test classpath
        byte[] result = HelpOption.getHelpContent(file);
        assertEquals("test help\n", new String(result));
    }

    // resolveMacros() -------------------------------------------------------------------------------------------------

    @Test
    public void resolveMacros_happyPath() throws Exception {

        String s = "abc@TESTMACRO@xyz";

        MockMacroResolver mmr = new MockMacroResolver();
        mmr.addMacro("TESTMACRO", "123");

        byte[] content = HelpOption.resolveMacros(s.getBytes(), mmr);

        assertEquals("abc123xyz", new String(content));
    }

    @Test
    public void resolveMacros_DifferentLines() throws Exception {

        String s = "abc@TESTMACRO\n@xyz";

        MockMacroResolver mmr = new MockMacroResolver();
        mmr.addMacro("TESTMACRO", "123");

        byte[] content = HelpOption.resolveMacros(s.getBytes(), mmr);

        assertEquals("abc@TESTMACRO\n@xyz", new String(content));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected HelpOption getOptionToTest(Character shortLiteral, String longLiteral) {

        return new HelpOption();
    }

    @Override
    protected Object getAppropriateValueForOptionToTest() {

        // any object
        return new Object();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

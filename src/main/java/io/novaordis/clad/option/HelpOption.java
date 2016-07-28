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

import io.novaordis.clad.application.ApplicationRuntime;
import io.novaordis.clad.command.Command;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class HelpOption extends OptionBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(HelpOption.class);

    public static final String LONG_LITERAL = "help";
    public static final Character SHORT_LITERAL = 'h';

    public static final String NO_COMMAND_HELP_FOUND_TEXT = "[warn]: no in-line help found for command";
    public static final String NO_APPLICATION_HELP_FOUND_TEXT = "[warn]: no in-line application help found";

    private static final Pattern MACRO_PATTERN = Pattern.compile("@\\w+@");

    // Static ----------------------------------------------------------------------------------------------------------

    // Package Protected Static ----------------------------------------------------------------------------------------

    static byte[] resolveMacros(byte[] helpContent, MacroResolver macroResolver) throws Exception {

        //
        // look for macros and resolve them recursively
        //

        String resolvedContent = new String(helpContent);

        while(true) {

            Matcher m = MACRO_PATTERN.matcher(resolvedContent);

            if (m.find()) {
                //
                // replace the macro and re-search
                //
                int start = m.start();
                int end = m.end();
                String macroName = resolvedContent.substring(start + 1, end - 1);
                String resolvedMacro = macroResolver.resolveMacro(macroName);

                if (resolvedMacro == null) {
                    //
                    // leave the macro in place so we know we couldn't resolve it
                    //
                    resolvedMacro = "@" + macroName + "@";
                }

                resolvedContent = resolvedContent.substring(0, start) + resolvedMacro + resolvedContent.substring(end);
                continue;
            }

            //
            // no more macros
            //

            break;
        }

        return resolvedContent.getBytes();
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private Command command;

    // if command is not null, it takes precedence
    private String commandName;

    private ApplicationRuntime application;

    private MacroResolver dynamicMacroResolver;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HelpOption() {
        super(SHORT_LITERAL, LONG_LITERAL);
        dynamicMacroResolver = new DynamicMacroResolver();
    }

    // OptionBase overrides --------------------------------------------------------------------------------------------

    @Override
    public String getValue() {

        return command == null ? null : command.getName();
    }

    @Override
    public void setValue(Object o) {

        // noop
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * Associates this help option with a pre-parsed command.
     *
     * @param command may be null.
     */
    public void setCommand(Command command) throws UserErrorException {

        if (this.command != null) {
            throw new IllegalStateException("command already set: " + this.command);
        }
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    /**
     * If command is not null, it takes precedence over the command name.
     */
    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    /**
     * Associates this help option with the application instance.
     *
     * @param application may be null.
     */
    public void setApplication(ApplicationRuntime application) {
        this.application = application;
    }

    public void displayHelp(OutputStream outputStream) throws Exception {

        if (command != null) {

            //
            // a command name was specified after "help" on command line
            //

            String helpFilePath = command.getHelpFilePath();
            byte[] helpContent = getHelpContent(helpFilePath);
            if (helpContent.length == 0) {
                helpContent = (NO_COMMAND_HELP_FOUND_TEXT + " '" + command.getName() + "'\n").getBytes();
            }
            outputStream.write(helpContent);
            outputStream.flush();
        }
        else {

            //
            // no command name was specified after "help" on command line
            //

            if (commandName != null) {
                throw new UserErrorException("unknown command: '" + commandName + "'");
            }

            //
            // generic application help + all commands
            //

            displayGenericApplicationHelpAndResolveMacros(outputStream);
        }
    }

    public void displayGenericApplicationHelpAndResolveMacros(OutputStream outputStream) throws Exception {

        //
        // display generic application help and resolve macros
        //

        String helpFilePath = application.getHelpFilePath();
        byte[] helpContent = getHelpContent(helpFilePath);
        if (helpContent.length == 0) {
            helpContent = (NO_APPLICATION_HELP_FOUND_TEXT + "'\n").getBytes();
        }
        helpContent = resolveMacros(helpContent, dynamicMacroResolver);
        outputStream.write(helpContent);
        outputStream.flush();
    }

    @Override
    public String toString() {

        return "-" + SHORT_LITERAL + "|--" + LONG_LITERAL;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Package Protected Static ----------------------------------------------------------------------------------------

    /**
     * @param helpFilePath the name of the file, relative to the class path, which contains in-line help.
     *
     * @return empty byte[] if no help content was found. The byte[] will always end with a '\n'.
     */
    static byte[] getHelpContent(String helpFilePath) throws Exception {

        InputStream is = HelpOption.class.getClassLoader().getResourceAsStream(helpFilePath);

        if (is == null) {
            //
            // help file not found
            //
            log.debug("help file " + helpFilePath + " not found");
            return new byte[0];
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        int r, last = -1;
        while ((r = is.read()) != -1) {
            baos.write(r);
            last = r;
        }

        if (last != '\n') {
            baos.write('\n');
        }

        return baos.toByteArray();
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

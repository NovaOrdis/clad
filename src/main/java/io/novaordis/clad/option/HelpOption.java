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
import io.novaordis.clad.InstanceFactory;
import io.novaordis.clad.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Command command;

    // if command is not null, it takes precedence
    private String commandName;

    private ApplicationRuntime application;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HelpOption() {
        super(SHORT_LITERAL, LONG_LITERAL);
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

            displayGenericApplicationHelpAndAllCommands(outputStream);
        }
    }

    public void displayGenericApplicationHelpAndAllCommands(OutputStream outputStream) throws Exception {

        InstanceFactory<Command> i = new InstanceFactory<>();

        Set<Command> commands = i.instances(
                Command.class, InstanceFactory.getClasspathJars(), InstanceFactory.getClasspathDirectories());

        List<Command> commandList = new ArrayList<>(commands);
        Collections.sort(commandList);

        //
        // display generic application help
        //

        String helpFilePath = application.getHelpFilePath();
        byte[] helpContent = getHelpContent(helpFilePath);
        if (helpContent.length == 0) {
            helpContent = (NO_APPLICATION_HELP_FOUND_TEXT + "'\n").getBytes();
        }
        outputStream.write(helpContent);
        outputStream.flush();

        //
        // iterate over the list of commands and determine the max display width
        //
        int width = maxDisplayWidth(commandList);

        outputStream.write("\n".getBytes());
        outputStream.write("Commands:\n".getBytes());
        outputStream.write("\n".getBytes());

        for(Command c: commandList) {

            helpFilePath = c.getHelpFilePath();
            helpContent = getHelpContent(helpFilePath);
            boolean helpAvailable = helpContent.length > 0;

            String format = "  %1$-" + width + "s";
            outputStream.write(String.format(format, c.getName()).getBytes());

            if (!helpAvailable) {
                outputStream.write(" (no in-line help found)".getBytes());
            }

            outputStream.write("\n".getBytes());
        }

        outputStream.write("\n".getBytes());
        outputStream.flush();
    }

    @Override
    public String toString() {

        return "-" + SHORT_LITERAL + "|--" + LONG_LITERAL;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Package Protected Static ----------------------------------------------------------------------------------------

    /**
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

    // Private Static --------------------------------------------------------------------------------------------------

    private static int maxDisplayWidth(List<Command> commandList) {
        int width = -1;
        for(Command c: commandList) {
            if (c.getName().length() > width) {
                width = c.getName().length();
            }
        }
        return width;
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}

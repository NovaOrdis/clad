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

import io.novaordis.clad.Command;
import io.novaordis.clad.UserErrorException;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class HelpOption extends OptionBase {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String LONG_LITERAL = "help";
    public static final Character SHORT_LITERAL = 'h';

    public static final String NO_HELP_FOUND_TEXT = "[warn]: no in-line help found for command";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Command command;

    // if command is not null, it takes precedence
    private String commandName;

    // Constructors ----------------------------------------------------------------------------------------------------

    public HelpOption() {
        super(SHORT_LITERAL, LONG_LITERAL);
    }

    // OptionBase overrides --------------------------------------------------------------------------------------------

    @Override
    public String getValue() {

        return command == null ? null : command.getName();
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

    public void displayHelp(OutputStream outputStream) throws Exception {

        if (command != null) {

            // pull the help content

            Class c = command.getClass();
            String name = c.getName();

            int i = name.lastIndexOf('.');
            String helpFileName = name.substring(i + 1);
            helpFileName = helpFileName.replace("Command", "").toLowerCase() + ".txt";
            String path = name.substring(0, i);
            path = path.replace(".", File.separator);
            helpFileName = path + File.separator + helpFileName;

            InputStream is = c.getClassLoader().getResourceAsStream(helpFileName);

            if (is == null) {
                // help file not found
                String msg = NO_HELP_FOUND_TEXT + " '" + command.getName() + "'\n";
                outputStream.write(msg.getBytes());
                outputStream.flush();
            }
            else {
                int r, last = -1;
                while ((r = is.read()) != -1) {
                    outputStream.write(r);
                    last = r;
                }

                if (last != '\n') {
                    outputStream.write('\n');
                }
                outputStream.flush();
            }
        }
        else {

            if (commandName != null) {
                throw new UserErrorException("unknown command: '" + commandName + "'");
            }

            //
            // all commands help
            //

            displayAllCommandsHelp(outputStream);
        }
    }

    public void displayAllCommandsHelp(OutputStream outputStream) throws Exception {

        outputStream.write("ALL COMMANDS HELP\n".getBytes());
        outputStream.flush();
    }

    @Override
    public String toString() {

        return "-" + SHORT_LITERAL + "|--" + LONG_LITERAL;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

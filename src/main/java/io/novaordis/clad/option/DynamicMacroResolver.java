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

import io.novaordis.clad.InstanceFactory;
import io.novaordis.clad.command.Command;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/6/16
 */
public class DynamicMacroResolver implements MacroResolver {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String COMMANDS = "COMMANDS";

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // MacroResolver implementation ------------------------------------------------------------------------------------

    @Override
    public String resolveMacro(String macroName) throws Exception {

        if (!COMMANDS.equals(macroName)) {
            return null;
        }

        String s = "";

        InstanceFactory<Command> i = new InstanceFactory<>();

        Set<Command> commands = i.instances(
                Command.class, InstanceFactory.getClasspathJars(), InstanceFactory.getClasspathDirectories());

        List<Command> commandList = new ArrayList<>(commands);
        Collections.sort(commandList);

        //
        // iterate over the list of commands and determine the max display width
        //
        int width = maxDisplayWidth(commandList);

        s += "Commands:\n\n";

        for(Command c: commandList) {

            String helpFilePath = c.getHelpFilePath();
            byte[] helpContent = HelpOption.getHelpContent(helpFilePath);
            boolean helpAvailable = helpContent.length > 0;

            String format = "  %1$-" + width + "s";

            s += String.format(format, c.getName());

            if (!helpAvailable) {

                s += " (no in-line help found)";
            }

            s += "\n";
        }

        return s;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

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

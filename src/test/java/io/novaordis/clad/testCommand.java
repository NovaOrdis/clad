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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TestCommand implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<String> commandLineArguments;

    // Constructors ----------------------------------------------------------------------------------------------------

    public TestCommand() {

        this.commandLineArguments = new ArrayList<>();
    }


    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void applyCommandLineArguments(List<String> commandLineArguments) throws Exception {

        this.commandLineArguments.addAll(commandLineArguments);
    }

    @Override
    public void execute() throws UserErrorException {
        throw new RuntimeException("execute() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public List<String> getCommandLineArguments() {
        return commandLineArguments;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

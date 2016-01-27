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

    private static final List<Option> globalOptionsInjectedByExecution = new ArrayList<>();
    private static final List<Option> commandOptionsInjectedByExecution = new ArrayList<>();

    public static List<Option> getGlobalOptionsInjectedByExecution() {
        return globalOptionsInjectedByExecution;
    }

    public static List<Option> getCommandOptionsInjectedByExecution() {
        return commandOptionsInjectedByExecution;
    }

    public static void clear() {

        globalOptionsInjectedByExecution.clear();
        commandOptionsInjectedByExecution.clear();
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Option> commandOptions;

    // Constructors ----------------------------------------------------------------------------------------------------

    public TestCommand() {

        this.commandOptions = new ArrayList<>();
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void injectCommandOptions(List<Option> options) throws Exception {

        this.commandOptions.addAll(options);
    }

    @Override
    public void execute(Configuration configuration) throws UserErrorException {

        //
        // inject global options and command options into the static lists for testing
        //

        globalOptionsInjectedByExecution.addAll(configuration.getGlobalOptions());
        commandOptionsInjectedByExecution.addAll(commandOptions);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public List<Option> getCommandOptions() {
        return commandOptions;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

package io.novaordis.clad.command;

import io.novaordis.clad.ApplicationRuntime;
import io.novaordis.clad.Configuration;
import io.novaordis.clad.UserErrorException;
import io.novaordis.clad.command.CommandBase;
import io.novaordis.clad.option.Option;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TestCommand extends CommandBase {

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

    // Constructors ----------------------------------------------------------------------------------------------------

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void execute(Configuration configuration, ApplicationRuntime runtime) throws UserErrorException {

        //
        // inject global options and command options into the static lists for testing
        //

        globalOptionsInjectedByExecution.addAll(configuration.getGlobalOptions());
        commandOptionsInjectedByExecution.addAll(configuration.getCommandOptions());
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

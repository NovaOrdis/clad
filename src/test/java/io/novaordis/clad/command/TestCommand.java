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

import io.novaordis.clad.application.ApplicationRuntime;
import io.novaordis.clad.configuration.Configuration;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.clad.option.Option;
import io.novaordis.clad.option.StringOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TestCommand extends CommandBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    private static final List<Option> globalOptionsInjectedByExecution = new ArrayList<>();

    public static List<Option> getGlobalOptionsInjectedByExecution() {
        return globalOptionsInjectedByExecution;
    }

    public static void clear() {

        globalOptionsInjectedByExecution.clear();
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public Set<Option> requiredOptions() {

        return new HashSet<>(Collections.singletonList(new StringOption("required-test-command-option")));
    }

    @Override
    public Set<Option> optionalOptions() {

        return new HashSet<>(Collections.singletonList(new StringOption('t')));
    }

    @Override
    public void execute(Configuration configuration, ApplicationRuntime runtime) throws UserErrorException {

        //
        // inject global options and command options into the static lists for testing
        //

        globalOptionsInjectedByExecution.addAll(configuration.getGlobalOptions());
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

package io.novaordis.clad.application;

import io.novaordis.utilities.UserErrorException;
import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.option.Option;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TestApplicationRuntime extends ApplicationRuntimeBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    private static boolean initialized = false;

    private static Set<Option> optionalGlobalOptions = new HashSet<>();

    public static boolean isInitialized() {

        return initialized;
    }

    public static void clear() {

        initialized = false;
        optionalGlobalOptions.clear();
    }

    public static void addOptionalGlobalOption(Option option) {
        optionalGlobalOptions.add(option);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // ApplicationRuntime implementation -------------------------------------------------------------------------------

    @Override
    public String getDefaultCommandName() {
        return null;
    }

    @Override
    public Set<Option> requiredGlobalOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<Option> optionalGlobalOptions() {
        return optionalGlobalOptions;
    }

    @Override
    public void init(Configuration configuration) throws UserErrorException {

        initialized = true;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

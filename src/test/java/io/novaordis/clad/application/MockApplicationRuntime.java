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

import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.option.Option;

import java.util.Collections;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/8/16
 */
public class MockApplicationRuntime extends ApplicationRuntimeBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // ApplicationRuntimeBase overrides --------------------------------------------------------------------------------

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
        return Collections.emptySet();
    }

    @Override
    public Configuration init(Configuration configuration) throws Exception {
        // noop
        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

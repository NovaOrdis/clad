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

/**
 * The command line applications wishing to use the framework must expose a class implementing ApplicationRuntime.
 *
 * The "official" name of the application will be inferred from the prefix of that simple class name. Example: If
 * MockApplicationRuntime is found on the classpath, then the application name is assumed to be "mock".
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public interface ApplicationRuntime {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the default command name. The command implementation must exist somewhere on the classpath. May
     * return null.
     */
    String getDefaultCommandName();

    void init(Configuration configuration) throws Exception;

}

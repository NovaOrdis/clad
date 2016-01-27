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

import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class ConfigurationImpl implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String applicationName;
    private List<Option> globalOptions;
    private List<Option> commandOptions;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ConfigurationImpl() throws UserErrorException {

        //
        // we expect to find the application name in the environment
        //

        this.applicationName = System.getProperty(APPLICATION_NAME_SYSTEM_PROPERTY_NAME);

        if (applicationName == null) {
            throw new UserErrorException("no '" + APPLICATION_NAME_SYSTEM_PROPERTY_NAME + "' system property set");
        }
    }

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public String getApplicationName() {

        return applicationName;
    }

    @Override
    public List<Option> getGlobalOptions() {

        return globalOptions;
    }

    @Override
    public List<Option> getCommandOptions() {

        return commandOptions;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    void setGlobalOptions(List<Option> globalOptions) {
        this.globalOptions = globalOptions;
    }

    void setCommandOptions(List<Option> commandOptions) {
        this.commandOptions = commandOptions;
    }

    void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

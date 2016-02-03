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

package io.novaordis.clad.configuration;

import io.novaordis.clad.UserErrorException;
import io.novaordis.clad.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class ConfigurationImpl implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ConfigurationImpl.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String applicationName;
    private List<Option> globalOptions;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ConfigurationImpl() throws UserErrorException {

        //
        // we expect to find the application name in the environment
        //

        this.applicationName = System.getProperty(APPLICATION_NAME_SYSTEM_PROPERTY_NAME);

        if (applicationName == null) {
            throw new UserErrorException("no '" + APPLICATION_NAME_SYSTEM_PROPERTY_NAME + "' system property set");
        }

        log.debug(this + " constructed");
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

    // Public ----------------------------------------------------------------------------------------------------------

    public void setGlobalOptions(List<Option> globalOptions) {
        this.globalOptions = globalOptions;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public String toString() {
        return "ConfigurationImpl[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

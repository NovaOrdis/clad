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

import io.novaordis.clad.option.VerboseOption;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.clad.option.Option;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private Map<String, String> genericLabels;

    // Constructors ----------------------------------------------------------------------------------------------------

    public ConfigurationImpl() throws UserErrorException {

        //
        // we expect to find the application name in the environment
        //

        this.applicationName = System.getProperty(APPLICATION_NAME_SYSTEM_PROPERTY_NAME);

        if (applicationName == null) {
            throw new UserErrorException("no '" + APPLICATION_NAME_SYSTEM_PROPERTY_NAME + "' system property set");
        }

        this.globalOptions = new ArrayList<>();

        this.genericLabels = new HashMap<>();

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

    @Override
    public Option getGlobalOption(Option model) {

        if (model == null) {
            return null;
        }

        for(Option o: globalOptions) {

            if (model.equals(o)) {
                return o;
            }
        }

        return null;
    }

    @Override
    public boolean isVerbose() {

        for(Option o: globalOptions) {

            if (o instanceof VerboseOption) {

                return true;
            }
        }

        return false;
    }

    @Override
    public void set(String genericLabel, String value) {

        genericLabels.put(genericLabel, value);
    }

    @Override
    public String get(String genericLabel) {

        return genericLabels.get(genericLabel);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setGlobalOptions(List<Option> globalOptions) {

        if (globalOptions == null) {
            throw new IllegalArgumentException("null globalOptions list");
        }
        this.globalOptions = globalOptions;
    }

    public void addGlobalOption(Option o) {

        this.globalOptions.add(o);
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

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

import io.novaordis.clad.option.HelpOption;
import io.novaordis.clad.option.Option;

import java.util.List;

/**
 * During the initialization phase, the clad-based application runtime gathers configuration information from different
 * sources (command line options, the optional configuration file, etc) and exposes them internally via a Configuration
 * implementation. For more details see
 * {@linktourl https://kb.novaordis.com/index.php/Clad_User_Manual_-_Concepts#Application_Configuration}
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public interface Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    String APPLICATION_NAME_SYSTEM_PROPERTY_NAME = "application.name";

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * @return first Help option or null
     */
    static HelpOption findHelpOption(List<Option> options) {

        if (options == null) {
            return null;
        }

        for(Option o: options) {
            if (o instanceof HelpOption) {
                return ((HelpOption)o);
            }
        }

        return null;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * The application name. The application runtime class must implement
     * <tt>&lt;application-name&gt;ApplicationRuntime</tt>.
     */
    String getApplicationName();

    /**
     * The global configuration options specified as command line arguments, preceding the command name. They are
     * returned in the order they show up on command line.
     */
    List<Option> getGlobalOptions();

    /**
     * @return may return null if no option that matches the definition is found.
     */
    Option getGlobalOption(Option definition);


    /**
     * @return true if the application was configured to run in verbose mode (and all DEBUG information displayed
     * at stdout).
     */
    boolean isVerbose();

}

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
import io.novaordis.clad.option.HelpOption;
import io.novaordis.clad.option.Option;

import java.util.List;

/**
 * The configuration of the command execution context.
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
     * The application name. The application runtime class must implement <tt>&lt;applicationName&gt;ApplicationRuntime</tt>.
     */
    String getApplicationName();

    /**
     * The global configuration options specified as command line arguments, preceding the command name.
     */
    List<Option> getGlobalOptions();

    /**
     * Convenience method to query the global options list and return the option corresponding to an "equivalent" pair
     * of short and long literals. If an option corresponding to either the short literal or the long literal is
     * present, it will be returned. If more than one option corresponding to the short and the long literal specified
     * as arguments are present, the last one will take precedence and a warning will be issued.
     *
     * @return the Option for the equivalent literals.
     *
     * @exception UserErrorException this is the first time when we tell the configuration instance that two literal
     * (short and long) are equivalent, so if we have two different values for the same option, complain and stop.
     */
    Option getGlobalOption(Character shortLiteral, String longLiteral) throws UserErrorException;

    /**
     * The command options specified as command line arguments, immediately following the command name.
     */
    List<Option> getCommandOptions();

}

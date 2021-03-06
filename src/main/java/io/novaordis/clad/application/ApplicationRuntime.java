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

import io.novaordis.clad.command.Command;
import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.option.Option;
import io.novaordis.utilities.expressions.Scope;

import java.io.File;
import java.io.OutputStream;
import java.util.Set;

/**
 * The command line applications wishing to use the framework must expose a class implementing ApplicationRuntime.
 *
 * The "official" name of the application will be inferred from the prefix of that simple class name. Example: If
 * MockApplicationRuntime is found on the classpath, then the application name is assumed to be "mock".
 *
 * The application runtime instance is the root variable Scope in the hierarchy.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public interface ApplicationRuntime extends Console {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    String getName();

    /**
     * @return the default command name. The command implementation must exist somewhere on the classpath. May
     * return null.
     */
    String getDefaultCommandName();

    /**
     * @return the relative path of the help file associated with this application. The path is relative to classpath.
     */
    String getHelpFilePath();

    /**
     * This is how an application declares its required global options. The command line application framework will
     * parse the command line and throw an UserErrorException if any of the required global options are not found
     * either on the command line or in the configuration file.
     *
     * @see Command#requiredOptions() ()
     */
    Set<Option> requiredGlobalOptions();

    /**
     * This is how an application declares its optional global options. The command line application framework will
     * parse the command line and the associated configuration file and throw an UserErrorException it finds a global
     * option that is not among the required global options or optional global options.
     *
     * @see Command#optionalOptions() ()
     */
    Set<Option> optionalGlobalOptions();

    /**
     * The method gives the application a chance to wrap the configuration into an application-specific and
     * install it internally, if it chooses so.
     */
    void init(Configuration configuration) throws Exception;

    /**
     * @return the application's configuration. May return null if the application was not initialized (init() was
     * not executed on this instance).
     *
     * @see ApplicationRuntime#init(Configuration)
     */
    Configuration getConfiguration();

    /**
     * Allows plugging an external stdout stream.
     */
    void setStdoutOutputStream(OutputStream outputStream);

    OutputStream getStdoutOutputStream();

    /**
     * Allows plugging an external stderr stream.
     */
    void setStderrOutputStream(OutputStream outputStream);

    OutputStream getStderrOutputStream();

    /**
     * @return the current directory the application runs from.
     */
    File getCurrentDirectory();

    /**
     * The scope is configured to resolve environment properties by default.
     */
    Scope getRootScope();

}

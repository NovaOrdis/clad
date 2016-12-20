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
import io.novaordis.utilities.variable.VariableFormatException;
import io.novaordis.utilities.variable.VariableProvider;

import java.io.File;
import java.io.OutputStream;
import java.util.Set;

/**
 * The command line applications wishing to use the framework must expose a class implementing ApplicationRuntime.
 *
 * The "official" name of the application will be inferred from the prefix of that simple class name. Example: If
 * MockApplicationRuntime is found on the classpath, then the application name is assumed to be "mock".
 *
 * The application runtime instance is the root VariableProvider in the hierarchy.
 *
 * @see VariableProvider
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public interface ApplicationRuntime extends VariableProvider {

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
     * The method returns configuration instance to give the application a chance to wrap the configuration into
     * an application-specific implementation if it chooses so. Returning null or the configuration instance that
     * was passed as argument is fine.
     *
     * TODO this is a first step towards the full refactoring of the configuration support, with the goal of
     * giving the application control over the configuration implementation.
     *
     * @param configuration the initial configuration instance built by the clad runtime.
     */
    Configuration init(Configuration configuration) throws Exception;

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
     * Sends to given string to stdout in "info" mode, followed by a new line.
     */
    void info(String s);

    /**
     * Sends to given string to stdout in "warning" mode, followed by a new line.
     */
    void warn(String s);

    /**
     * Sends to given string to stderr in "error" mode, followed by a new line.
     */
    void error(String s);

    /**
     * @return the current directory the application runs from.
     */
    File getCurrentDirectory();

    /**
     * The method replaces the runtime variables found in the given string with values existing in the runtime.
     * If no corresponding value is found, no replacement is made, the variable is left in the ${var_name} format.
     *
     * See ${linkUrl https://kb.novaordis.com/index.php/Clad_User_Manual_-_Concepts#Variable_Support} for more details.
     *
     * @return the string with runtime variables (those that could be resolved) replaced.
     */
    String resolveVariables(String s) throws VariableFormatException;

}

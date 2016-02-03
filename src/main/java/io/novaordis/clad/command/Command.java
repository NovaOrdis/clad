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

package io.novaordis.clad.command;

import io.novaordis.clad.application.ApplicationRuntime;
import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.UserErrorException;
import io.novaordis.clad.option.Option;

import java.util.List;
import java.util.Set;

/**
 * A command implementation must have a non-argument public constructor, this is how the command line application
 * framework instantiates commands.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/16
 */
public interface Command extends Comparable<Command> {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the name (literal) of the command, as specified on the command line.
     */
    String getName();

    /**
     * @return the relative path of the help file associated with this command. The path is relative to classpath.
     */
    String getHelpFilePath();

    /**
     * Specifies whether the command needs the runtime (and specifically, the runtime initialized) to execute. This
     * piece of information is important to the framework because runtime initialization is application-specific and
     * may throw application-specific exception that have nothing to do with the command's execution. In general,
     * a command "needs" the runtime.
     */
    boolean needsRuntime();

    /**
     * The declaration of this command's required options.
     *
     * @see ApplicationRuntime#requiredGlobalOptions()
     */
    Set<Option> requiredOptions();

    /**
     * The declaration of this command's optional options.
     *
     * @see ApplicationRuntime#optionalGlobalOptions()
     */
    Set<Option> optionalOptions();

    /**
     * All command line arguments remaining after global option processing are fed into the command so the command
     * instance has a chance to identify the options that belong to it and configure itself.
     *
     * The method will remove the known arguments from the list.
     */
    void configure(int from, List<String> commandLineArgs) throws Exception;

    /**
     * @return the actual command options in the order they show up on command line, in the order they show up on
     *  command line.
     */
    List<Option> getOptions();

    /**
     * May return null if not such option exists.
     */
    Option getOption(Option model);

    void setOption(Option o);

    /**
     * @param configuration the command execution context configuration. Offers access to global options and command
     *                      options.
     *
     * @param runtime the command execution runtime. Guaranteed to be called init() at this time.
     *
     * @exception UserErrorException - if thrown, the framework will display the message after [error]: and exit with
     *  a non-zero exit code.
     *
     * @exception Exception any other kind of error.
     */
    void execute(Configuration configuration, ApplicationRuntime runtime) throws Exception;

}

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

/**
 * A command implementation must have a non-argument public constructor, this is how the command line application
 * framework instantiates commands.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/16
 */
public interface Command {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the name (literal) of the command, as specified on the command line.
     */
    String getName();

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

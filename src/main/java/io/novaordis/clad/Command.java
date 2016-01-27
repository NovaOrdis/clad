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
     * Called by the framework immediately after instantiation. It feeds the command options specified as command line
     * arguments (everything following the command name). This method is called by the command line application
     * framework immediately after instantiation.
     */
    void injectCommandOptions(List<Option> commandOption) throws Exception;

    /**
     * @param configuration the command execution context configuration.
     */
    void execute(Configuration configuration) throws UserErrorException;

}

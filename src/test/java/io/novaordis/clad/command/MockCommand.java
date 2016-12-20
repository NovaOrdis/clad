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
import io.novaordis.clad.option.Option;
import io.novaordis.utilities.NotYetImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Use it every time when you need a place holder for a command.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/20/16
 */
// instantiated with reflection
@SuppressWarnings("unused")
public class MockCommand implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(MockCommand.class);

    // Static ----------------------------------------------------------------------------------------------------------

    private static boolean needsRuntime;

    static {

        needsRuntime = false;
    }

    public static void reset() {

        needsRuntime = false;
    }

    public static void setNeedsRuntime(boolean b) {

        needsRuntime = b;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration configuration;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public String getName() {

        return "mock";
    }

    @Override
    public String getHelpFilePath() {

        return "no-such-file.txt";
    }

    @Override
    public boolean needsRuntime() {

        return needsRuntime;
    }

    @Override
    public Set<Option> requiredOptions() {

        return Collections.emptySet();
    }

    @Override
    public Set<Option> optionalOptions() {

        return Collections.emptySet();
    }

    @Override
    public void configure(int from, List<String> commandLineArgs) throws Exception {

        //
        // noop
        //

        log.info(this + " configured");
    }

    @Override
    public List<Option> getOptions() {

        return Collections.emptyList();
    }

    @Override
    public Option getOption(Option model) {
        throw new NotYetImplementedException("getOption() NOT YET IMPLEMENTED");
    }

    @Override
    public void setOption(Option o) {
        throw new NotYetImplementedException("setOption() NOT YET IMPLEMENTED");
    }

    @Override
    public void execute(Configuration configuration, ApplicationRuntime runtime) throws Exception {

        log.info(this + " executing ...");

        this.configuration = configuration;
    }

    @Override
    public int compareTo(Command o) {

        return 0;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Configuration getConfigurationProvidedDuringTheLastExecution() {

        return configuration;
    }

    @Override
    public String toString() {

        return "MockCommand[" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.configuration.MockConfiguration;
import io.novaordis.clad.option.Option;
import io.novaordis.utilities.UserErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TestApplicationRuntime extends ApplicationRuntimeBase {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(TestApplicationRuntime.class);

    // Static ----------------------------------------------------------------------------------------------------------

    private static boolean initialized;

    private static Set<Option> optionalGlobalOptions;

    private static String defaultCommandName;

    private static ApplicationInitBehavior initBehavior;

    static {

        initialized = false;
        optionalGlobalOptions = new HashSet<>();
        defaultCommandName = null;
        initBehavior = ApplicationInitBehavior.INSTALLS_SAME_INSTANCE;
    }

    public static boolean isInitialized() {

        return initialized;
    }

    public static void reset() {

        initialized = false;
        optionalGlobalOptions.clear();
        defaultCommandName = null;
    }

    public static void addOptionalGlobalOption(Option option) {
        optionalGlobalOptions.add(option);
    }

    public static void installDefaultCommandName(String name) {

        defaultCommandName = name;
    }

    public static void setInitBehavior(ApplicationInitBehavior behavior) {

        initBehavior = behavior;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // ApplicationRuntime implementation -------------------------------------------------------------------------------

    @Override
    public String getDefaultCommandName() {

        return defaultCommandName;
    }

    @Override
    public Set<Option> requiredGlobalOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<Option> optionalGlobalOptions() {
        return optionalGlobalOptions;
    }

    @Override
    public void init(Configuration configuration) throws UserErrorException {

        initialized = true;

        if (ApplicationInitBehavior.INSTALLS_SAME_INSTANCE.equals(initBehavior)) {

            log.info("init installs the given configuration instance");

            setConfiguration(configuration);
        }
        else if (ApplicationInitBehavior.INSTALLS_WRAPPER.equals(initBehavior)) {

            log.info("init installs wrapper");

            setConfiguration(new MockConfiguration(configuration));
        }
        else {

            throw new IllegalStateException("invalid init behaviour: " + initBehavior);
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

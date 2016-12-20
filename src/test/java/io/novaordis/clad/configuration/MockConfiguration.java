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

import io.novaordis.clad.option.Option;
import io.novaordis.utilities.NotYetImplementedException;

import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 12/20/16
 */
public class MockConfiguration implements Configuration {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Configuration delegate;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockConfiguration() {

        this(null);
    }

    /**
     * @param delegate may be null.
     */
    public MockConfiguration(Configuration delegate) {

        this.delegate = delegate;
    }

    // Configuration implementation ------------------------------------------------------------------------------------

    @Override
    public String getApplicationName() {

        if (delegate != null) {

            return delegate.getApplicationName();
        }

        throw new NotYetImplementedException("getApplicationName() NOT YET IMPLEMENTED");
    }

    @Override
    public List<Option> getGlobalOptions() {

        if (delegate != null) {

            return delegate.getGlobalOptions();
        }

        throw new NotYetImplementedException("getGlobalOptions() NOT YET IMPLEMENTED");
    }

    @Override
    public Option getGlobalOption(Option definition) {

        if (delegate != null) {

            return delegate.getGlobalOption(definition);
        }

        throw new NotYetImplementedException("getGlobalOption() NOT YET IMPLEMENTED");
    }

    @Override
    public boolean isVerbose() {

        if (delegate != null) {

            return delegate.isVerbose();
        }

        throw new NotYetImplementedException("isVerbose() NOT YET IMPLEMENTED");
    }

    @Override
    public void set(String configurationLabel, String value) {

        if (delegate != null) {

            delegate.set(configurationLabel, value);
        }

        throw new NotYetImplementedException("set() NOT YET IMPLEMENTED");
    }

    @Override
    public String get(String configurationLabel) {

        if (delegate != null) {

            return delegate.get(configurationLabel);
        }

        throw new NotYetImplementedException("get() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public Configuration getDelegate() {

        return delegate;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

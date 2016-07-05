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
import io.novaordis.clad.option.StringOption;
import org.junit.Test;
import org.slf4j.Logger;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class ConfigurationImplTest extends ConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = org.slf4j.LoggerFactory.getLogger(ConfigurationImplTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void setNullGlobalOptions() throws Exception {

        ConfigurationImpl c = new ConfigurationImpl();

        try {
            c.setGlobalOptions(null);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void addGlobalOption() throws Exception {

        ConfigurationImpl c = new ConfigurationImpl();

        assertTrue(c.getGlobalOptions().isEmpty());

        c.addGlobalOption(new StringOption(null, "test", "test-value"));

        StringOption o = (StringOption)c.getGlobalOption(new StringOption("test"));
        assertEquals("test-value", o.getValue());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected ConfigurationImpl getConfigurationToTest(List<Option> global) throws Exception {

        ConfigurationImpl c = new ConfigurationImpl();
        c.setGlobalOptions(global);
        return c;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

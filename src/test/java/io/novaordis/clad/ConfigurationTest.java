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

import io.novaordis.clad.option.HelpOption;
import io.novaordis.clad.option.Option;
import io.novaordis.clad.option.StringOption;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public abstract class ConfigurationTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ConfigurationTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void initialization() throws Exception {

        try {

            System.setProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME, "something");

            StringOption gso = new StringOption('g');
            gso.setValue("gval");
            StringOption cso = new StringOption('c');
            cso.setValue("cval");

            Configuration c = getConfigurationToTest(
                    new ArrayList<>(Collections.singletonList(gso)),
                    new ArrayList<>(Collections.singletonList(cso)));

            assertEquals(1, c.getGlobalOptions().size());
            assertEquals(1, c.getCommandOptions().size());

            assertEquals("gval", ((StringOption) c.getGlobalOptions().get(0)).getValue());
            assertEquals("cval", ((StringOption) c.getCommandOptions().get(0)).getValue());

            assertEquals("something", c.getApplicationName());
        }
        finally {

            System.clearProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME);
        }
    }

    @Test
    public void noApplicationName() throws Exception {

        try {

            getConfigurationToTest(Collections.emptyList(), Collections.emptyList());
            fail("should have thrown exception, no application name");
        }
        catch(UserErrorException e) {
            log.info(e.getMessage());
        }
    }

    // findHelpOption() ------------------------------------------------------------------------------------------------

    @Test
    public void findHelpOption_Null() throws Exception {
        assertNull(Configuration.findHelpOption(null));
    }

    @Test
    public void findHelpOption_Empty() throws Exception {
        assertNull(Configuration.findHelpOption(Collections.emptyList()));
    }

    @Test
    public void findHelpOption() throws Exception {

        HelpOption o = new HelpOption();
        assertEquals(o, Configuration.findHelpOption(Collections.singletonList(o)));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Configuration getConfigurationToTest(List<Option> global, List<Option> command) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

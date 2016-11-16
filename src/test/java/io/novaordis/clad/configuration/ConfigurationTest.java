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

import io.novaordis.utilities.UserErrorException;
import io.novaordis.clad.option.BooleanOption;
import io.novaordis.clad.option.HelpOption;
import io.novaordis.clad.option.Option;
import io.novaordis.clad.option.StringOption;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
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

    @Before
    public void setup() {
        System.setProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME, "something");
    }

    @After
    public void tearDown() {
        System.clearProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void initialization() throws Exception {

        StringOption gso = new StringOption('g');
        gso.setValue("gval");

        Configuration c = getConfigurationToTest(new ArrayList<>(Collections.singletonList(gso)));

        assertEquals(1, c.getGlobalOptions().size());

        assertEquals("gval", ((StringOption) c.getGlobalOptions().get(0)).getValue());

        assertEquals("something", c.getApplicationName());

        assertFalse(c.isVerbose());
    }

    @Test
    public void noApplicationName() throws Exception {

        try {
            System.clearProperty(Configuration.APPLICATION_NAME_SYSTEM_PROPERTY_NAME);
            getConfigurationToTest(Collections.emptyList());
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

    // getGlobalOptions() ----------------------------------------------------------------------------------------------

    @Test
    public void getGlobalOptions() throws Exception {

        List<Option> global = Collections.emptyList();
        Configuration c = getConfigurationToTest(global);
        assertFalse(c.getGlobalOptions().contains(new StringOption('o')));
    }

    @Test
    public void getGlobalOptions2() throws Exception {

        StringOption so = new StringOption('o');
        List<Option> global = Arrays.asList(new StringOption('a'), new StringOption('b'), so);
        Configuration c = getConfigurationToTest(global);

        assertTrue(c.getGlobalOptions().contains(new StringOption('o')));
        assertTrue(c.getGlobalOptions().contains(new StringOption('o', "option")));
    }

    @Test
    public void getGlobalOption_EquivalentLiterals_LongPresent() throws Exception {

        StringOption so = new StringOption("option");
        List<Option> global = Arrays.asList(new StringOption("something"), new StringOption('b'), so);
        Configuration c = getConfigurationToTest(global);

        assertTrue(c.getGlobalOptions().contains(new StringOption("option")));
        assertTrue(c.getGlobalOptions().contains(new StringOption('o', "option")));
    }

    // getGlobalOption() -----------------------------------------------------------------------------------------------

    @Test
    public void getGlobalOption() throws Exception {

        StringOption so = new StringOption('o');
        so.setValue("test");
        List<Option> global = Collections.singletonList(so);
        Configuration c = getConfigurationToTest(global);

        assertNull(c.getGlobalOption(new BooleanOption('o')));

        StringOption so2 = (StringOption)c.getGlobalOption(new StringOption('o'));

        assertEquals(so, so2);
        assertEquals("test", so2.getValue());
    }

    // generic configuration labels ------------------------------------------------------------------------------------

    @Test
    public void get_NoSuchLabel() throws Exception {

        Configuration c = getConfigurationToTest(Collections.emptyList());

        assertNull(c.get("I.AM.SURE.THERE.IS.NO.SUCH.CONFIGURATION.LABEL"));
    }

    @Test
    public void genericConfigurationLabelGetSet() throws Exception {

        Configuration c = getConfigurationToTest(Collections.emptyList());

        assertNull(c.get("test"));

        c.set("test", "something");

        assertEquals("something", c.get("test"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected abstract Configuration getConfigurationToTest(List<Option> global) throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

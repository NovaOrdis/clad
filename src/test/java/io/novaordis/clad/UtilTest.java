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

import io.novaordis.clad.application.ApplicationRuntime;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/31/16
 */
public class UtilTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // normalizeApplicationName() --------------------------------------------------------------------------------------

    @Test
    public void normalizeApplicationName() throws Exception {

        Assert.assertEquals("Test", Util.normalizeLabel("test"));
    }

    @Test
    public void normalizeApplicationName_Dash() throws Exception {

        assertEquals("AppOne", Util.normalizeLabel("app-one"));
    }

    @Test
    public void normalizeApplicationName_DashAtTheEnd() throws Exception {

        assertEquals("App", Util.normalizeLabel("app-"));
    }

    @Test
    public void normalizeApplicationName_TwoDashes() throws Exception {

        assertEquals("AppOneTwo", Util.normalizeLabel("app-one-two"));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    //protected abstract ApplicationRuntime getApplicationRuntimeToTest() throws Exception;

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

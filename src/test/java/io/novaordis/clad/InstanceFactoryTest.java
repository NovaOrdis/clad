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

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/29/16
 */
public class InstanceFactoryTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(InstanceFactoryTest.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void getCommand() throws Exception {

        Command command = InstanceFactory.getCommand("test");
        assertNotNull(command);
        assertTrue(command instanceof TestCommand);
    }

    @Test
    public void getCommand_NoSuchCommand() throws Exception {
        assertNull(InstanceFactory.getCommand("no-such-command"));
    }

    // getFileNames() --------------------------------------------------------------------------------------------------

    @Test
    public void getFileNames() throws Exception {

        String basedir = System.getProperty("basedir");
        assertNotNull(basedir);

        File data = new File(basedir, "src/test/resources/data");
        assertTrue(data.isDirectory());

        List<String> names = InstanceFactory.getFileNames(data.getPath(), data);
        assertEquals(2, names.size());

        String name = data.getPath() + File.separator + "testDir" + File.separator  + "A.txt";
        assertEquals(name, names.get(0));
        name = data.getPath() + File.separator + "testDir" + File.separator  + "testSubDir" +  File.separator  + "B.txt";
        assertEquals(name, names.get(1));
    }

    // toSimpleClassName() ---------------------------------------------------------------------------------------------

    @Test
    public void toSimpleClassName_Null() throws Exception {

        try {
            InstanceFactory.toSimpleClassName(null, "Something");
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            log.debug(e.getMessage());
        }

        try {
            InstanceFactory.toSimpleClassName("something", null);
            fail("should have thrown IllegalArgumentException");
        }
        catch(IllegalArgumentException e) {
            log.debug(e.getMessage());
        }
    }

    @Test
    public void toSimpleClassName() throws Exception {

        String s = InstanceFactory.toSimpleClassName("test", "Command");
        assertEquals("TestCommand", s);
    }

    @Test
    public void toSimpleClassName2() throws Exception {

        String s = InstanceFactory.toSimpleClassName("Test", "Command");
        assertEquals("TestCommand", s);
    }

    @Test
    public void toSimpleClassName3() throws Exception {

        String s = InstanceFactory.toSimpleClassName("TEST", "Command");
        assertEquals("TestCommand", s);
    }

    @Test
    public void toSimpleClassName4() throws Exception {

        String s = InstanceFactory.toSimpleClassName("test", "ApplicationRuntime");
        assertEquals("TestApplicationRuntime", s);
    }

    @Test
    public void toSimpleClassName5() throws Exception {

        String s = InstanceFactory.toSimpleClassName("TEST", "ApplicationRuntime");
        assertEquals("TestApplicationRuntime", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

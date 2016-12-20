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

import io.novaordis.clad.MockOutputStream;
import io.novaordis.utilities.Files;
import io.novaordis.utilities.version.VersionUtilities;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/27/16
 */
public class VersionCommandTest extends CommandTest {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(VersionCommand.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void execute_noVERSIONFile() throws Exception {

        //
        // no VERSION file
        //

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        VersionCommand c = new VersionCommand();
        c.setOutputStream(baos);

        c.execute(null);

        baos.close();
        byte[] bytes = baos.toByteArray();
        String s = new String(bytes);

        log.info(s);

        assertEquals("version N/A\nrelease date N/A\n", s);
    }

    @Test
    public void execute_VERSIONFilePresent() throws Exception {

        String basedir = System.getProperty("basedir");
        assertNotNull(basedir);
        File testClassesDir = new File(basedir, "target/test-classes");
        assertTrue(testClassesDir.isDirectory());

        //
        // create the VERSION file
        //

        File versionFile = new File(testClassesDir, VersionUtilities.DEFAULT_VERSION_FILE_NAME);

        String content = "version=test-version\nrelease_date=01/01/01\n";
        String expected = "version test-version\nrelease date 01/01/01\n";

        try {

            assertTrue(Files.write(versionFile, content));

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            VersionCommand c = new VersionCommand();
            c.setOutputStream(baos);

            c.execute(null);

            baos.close();
            byte[] bytes = baos.toByteArray();
            String s = new String(bytes);

            log.info(s);

            assertEquals(expected, s);
        }
        finally {

            assertTrue(versionFile.delete());
        }
    }

    @Test
    public void outputStream() throws Exception {

        VersionCommand c = getCommandToTest();

        assertNull(c.getOutputStream());

        MockOutputStream mos = new MockOutputStream();
        c.setOutputStream(mos);
        assertEquals(mos, c.getOutputStream());
    }

    @Test
    public void configure_getOptions() throws Exception {

        VersionCommand c = getCommandToTest();

        assertTrue(c.getOptions().isEmpty());

        //
        // feed it random stuff, it should be ignored
        //

        List<String> args = new ArrayList<>(Arrays.asList(
                "-a", "--something=somethingelse", "-c", "--output=something"));

        c.configure(1, args);
        assertTrue(c.getOptions().isEmpty());

        assertEquals(4, args.size());
        assertEquals("-a", args.get(0));
        assertEquals("--something=somethingelse", args.get(1));
        assertEquals("-c", args.get(2));
        assertEquals("--output=something", args.get(3));
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    @Override
    protected VersionCommand getCommandToTest() throws Exception {
        return new VersionCommand();
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

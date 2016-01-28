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

import io.novaordis.utilities.Files;
import io.novaordis.utilities.VersionUtilities;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/27/16
 */
public class VersionCommandTest {

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

        c.execute(null, null);

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

            c.execute(null, null);

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

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

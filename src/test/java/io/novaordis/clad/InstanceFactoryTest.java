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

import a.test.Sample1Command;
import b.test.Sample2Command;
import c.test.Sample3Command;
import io.novaordis.clad.command.Command;
import io.novaordis.clad.command.Test2Command;
import io.novaordis.clad.command.TestCommand;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.jar.JarFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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

        Util.normalizeLabelInvoked = false;
        Command command = InstanceFactory.getCommand("test");
        assertTrue(Util.normalizeLabelInvoked);
        assertNotNull(command);
        assertTrue(command instanceof TestCommand);
    }

    @Test
    public void getCommand_NoSuchCommand() throws Exception {

        Util.normalizeLabelInvoked = false;
        assertNull(InstanceFactory.getCommand("no-such-command"));
        assertTrue(Util.normalizeLabelInvoked);
    }

    @Test
    public void getCommand_InvalidCandidate_SmallerThanA() throws Exception {

        Util.normalizeLabelInvoked = false;
        Command c = InstanceFactory.getCommand("@something");
        assertFalse(Util.normalizeLabelInvoked);
        assertNull(c);
    }

    @Test
    public void getCommand_InvalidCandidate_BetweenZanda() throws Exception {

        Util.normalizeLabelInvoked = false;
        Command c = InstanceFactory.getCommand("{something");
        assertFalse(Util.normalizeLabelInvoked);
        assertNull(c);
    }

    @Test
    public void getCommand_InvalidCandidate_BiggerThanz() throws Exception {

        Util.normalizeLabelInvoked = false;
        Command c = InstanceFactory.getCommand("-something");
        assertFalse(Util.normalizeLabelInvoked);
        assertNull(c);
    }

    @Test
    public void getCommand_InvalidCandidate_UnbecomingCharacters() throws Exception {

        Util.normalizeLabelInvoked = false;
        Command c = InstanceFactory.getCommand("timestamp(time:yy/MM/dd");
        assertFalse(Util.normalizeLabelInvoked);
        assertNull(c);
    }

    // getFileNames() --------------------------------------------------------------------------------------------------

    @Test
    public void getFileNames() throws Exception {

        String basedir = System.getProperty("basedir");
        assertNotNull(basedir);

        File testDir = new File(basedir, "src/test/resources/data/testDir");
        assertTrue(testDir.isDirectory());

        List<String> names = InstanceFactory.getFileNames(testDir.getPath(), testDir);
        assertEquals(2, names.size());

        String name = testDir.getPath() + File.separator  + "A.txt";
        assertEquals(name, names.get(0));
        name = testDir.getPath() + File.separator  + "testSubDir" +  File.separator  + "B.txt";
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
        assertEquals("TESTCommand", s);
    }

    @Test
    public void toSimpleClassName4() throws Exception {

        String s = InstanceFactory.toSimpleClassName("test", "ApplicationRuntime");
        assertEquals("TestApplicationRuntime", s);
    }

    @Test
    public void toSimpleClassName5() throws Exception {

        String s = InstanceFactory.toSimpleClassName("TEST", "ApplicationRuntime");
        assertEquals("TESTApplicationRuntime", s);
    }

    // getFullyQualifiedClassNamesFromDirectories() --------------------------------------------------------------------

    @Test
    public void getFullyQualifiedClassNamesFromDirectories_InvalidPattern() throws Exception {

        List<File> dirs = Collections.emptyList();

        try {
            InstanceFactory.getFullyQualifiedClassNamesFromDirectories(".*\\..*2\\.class", dirs);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void getFullyQualifiedClassNamesFromDirectories_NoSuchClass() throws Exception {

        String s = System.getProperty("basedir");
        assertNotNull(s);
        File dir = new File(s, "target/test-classes");
        assertTrue(dir.isDirectory());

        List<File> dirs = Collections.singletonList(dir);

        List<String> names = InstanceFactory.getFullyQualifiedClassNamesFromDirectories(".*nosuchclass.*", dirs);

        assertTrue(names.isEmpty());
    }

    @Test
    public void getFullyQualifiedClassNamesFromDirectories_OneClassMatches() throws Exception {

        String s = System.getProperty("basedir");
        assertNotNull(s);
        File dir = new File(s, "target/test-classes");
        assertTrue(dir.isDirectory());

        List<File> dirs = Collections.singletonList(dir);

        List<String> names = InstanceFactory.getFullyQualifiedClassNamesFromDirectories(".*\\..*2", dirs);

        assertEquals(1, names.size());
        assertEquals("b.test.Example2", names.get(0));
    }

    @Test
    public void getFullyQualifiedClassNamesFromDirectories_MultipleClassesMatch() throws Exception {

        String s = System.getProperty("basedir");
        assertNotNull(s);
        File dir = new File(s, "target/test-classes");
        assertTrue(dir.isDirectory());

        List<File> dirs = Collections.singletonList(dir);

        List<String> names = InstanceFactory.getFullyQualifiedClassNamesFromDirectories(".*\\.Example.", dirs);

        assertEquals(3, names.size());
        assertEquals("a.test.Example1", names.get(0));
        assertEquals("b.test.Example2", names.get(1));
        assertEquals("c.test.Example3", names.get(2));
    }

    // getFullyQualifiedClassNamesFromJARs() ---------------------------------------------------------------------------

    @Test
    public void getFullyQualifiedClassNamesFromJars_InvalidPattern() throws Exception {

        List<JarFile> files = Collections.emptyList();

        try {
            InstanceFactory.getFullyQualifiedClassNamesFromJars(".*\\..*2\\.class", files);
            fail("should have thrown exception");
        }
        catch(IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    @Test
    public void getFullyQualifiedClassNamesFromJars_NoSuchClass() throws Exception {

        String s = System.getProperty("basedir");
        assertNotNull(s);
        File jarFile = new File(s, "src/test/resources/data/test.jar");
        assertTrue(jarFile.isFile());

        List<JarFile> files = Collections.singletonList(new JarFile(jarFile));

        List<String> names = InstanceFactory.getFullyQualifiedClassNamesFromJars(".*nosuchclass.*", files);

        assertTrue(names.isEmpty());
    }

    @Test
    public void getFullyQualifiedClassNamesFromJars_OneClassMatches() throws Exception {

        String s = System.getProperty("basedir");
        assertNotNull(s);
        File jarFile = new File(s, "src/test/resources/data/test.jar");
        assertTrue(jarFile.isFile());

        List<JarFile> files = Collections.singletonList(new JarFile(jarFile));

        List<String> names = InstanceFactory.getFullyQualifiedClassNamesFromJars(".*\\..*2", files);

        assertEquals(1, names.size());
        assertEquals("b.test.Example2", names.get(0));
    }

    @Test
    public void getFullyQualifiedClassNamesFromJars_MultipleClassesMatch() throws Exception {

        String s = System.getProperty("basedir");
        assertNotNull(s);
        File jarFile = new File(s, "src/test/resources/data/test.jar");
        assertTrue(jarFile.isFile());

        List<JarFile> files = Collections.singletonList(new JarFile(jarFile));

        List<String> names = InstanceFactory.getFullyQualifiedClassNamesFromJars(".*\\.Example.", files);

        assertEquals(3, names.size());
        assertEquals("a.test.Example1", names.get(0));
        assertEquals("b.test.Example2", names.get(1));
        assertEquals("c.test.Example3", names.get(2));
    }

    // instances() -----------------------------------------------------------------------------------------------------

    @Test
    public void instancesFromJAR() throws Exception {

        InstanceFactory<Command> commandFactory = new InstanceFactory<>();

        File jarFile = new File(System.getProperty("basedir"), "src/test/resources/data/test.jar");
        assertTrue(jarFile.isFile());
        List<JarFile> files = Collections.singletonList(new JarFile(jarFile));

        Set<Command> commands = commandFactory.instances(Command.class, files, Collections.emptyList());

        log.info("" + commands);

        assertEquals(5, commands.size());
        boolean sample1found = false;
        boolean sample2found = false;
        boolean sample3found = false;
        boolean testFound = false;
        boolean test2Found = false;

        for(Command c: commands) {
            if (c instanceof Sample1Command) {
                sample1found = true;
            }
            else if (c instanceof Sample2Command) {
                sample2found = true;
            }
            else if (c instanceof Sample3Command) {
                sample3found  = true;
            }
            else if (c instanceof TestCommand) {
                testFound  = true;
            }
            else if (c instanceof Test2Command) {
                test2Found  = true;
            }
            else {
                fail("unknown command: " + c);
            }
        }

        assertTrue(sample1found);
        assertTrue(sample2found);
        assertTrue(sample3found);
        assertTrue(testFound);
        assertTrue(test2Found);
    }

    @Test
    public void instancesFromDirectory() throws Exception {

        InstanceFactory<Command> commandFactory = new InstanceFactory<>();

        File directory = new File(System.getProperty("basedir"), "target/test-classes");
        assertTrue(directory.isDirectory());
        List<File> directories = Collections.singletonList(directory);

        Set<Command> commands = commandFactory.instances(Command.class, Collections.emptyList(), directories);

        assertEquals(7, commands.size());
        boolean sample1found = false;
        boolean sample2found = false;
        boolean sample3found = false;
        boolean testFound = false;
        boolean test2Found = false;

        for(Command c: commands) {
            if (c instanceof Sample1Command) {
                sample1found = true;
            }
            else if (c instanceof Sample2Command) {
                sample2found = true;
            }
            else if (c instanceof Sample3Command) {
                sample3found  = true;
            }
            else if (c instanceof TestCommand) {
                testFound  = true;
            }
            else if (c instanceof Test2Command) {
                test2Found  = true;
            }
            else {
                log.info("command that was not counted: " + c);
            }
        }

        assertTrue(sample1found);
        assertTrue(sample2found);
        assertTrue(sample3found);
        assertTrue(testFound);
        assertTrue(test2Found);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

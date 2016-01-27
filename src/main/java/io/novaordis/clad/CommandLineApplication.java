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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/16
 */
public abstract class CommandLineApplication {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CommandLineApplication.class);

    public static final String JAVA_CLASS_PATH_SYSTEM_PROPERTY_NAME = "java.class.path";
    public static final String PATH_SEPARATOR_SYSTEM_PROPERTY_NAME = "path.separator";

    public static final int DIRECTORIES_ARE_SEARCHED_FIRST = 0;
    public static final int JARS_ARE_SEARCHED_FIRST = 1;

    // Static ----------------------------------------------------------------------------------------------------------

    //
    // whether directories or the JARs from the class path are searched first.
    //
    public static int searchOrder = DIRECTORIES_ARE_SEARCHED_FIRST;

    public static void main(String[] args) throws Exception {

        //
        // identify and instantiate the command - the first command line argument that corresponds to a Command
        // implementation
        //

        List<String> commandLineArguments = new ArrayList<>(Arrays.asList(args));

        Command command = identifyCommand(commandLineArguments);

        List<String> globalOptions = new ArrayList<>(commandLineArguments);

        log.debug("command: " + command);

        log.debug("global options: " + globalOptions);

        if (command == null) {
            throw new UserErrorException("no command");
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * The method parses the command line arguments and attempts to identify the first command line argument that
     * can be mapped on a command.
     *
     * If no command is found, the method returns null.
     *
     * If a command is found, the method removes the rest of the arguments from the list received as parameter,
     * passes them to the command constructor and instantiates the command. This, the list passed as argument is left
     * with only the global arguments.
     *
     * @return the Command instance or null if no command was identified.
     */
    static Command identifyCommand(List<String> commandLineArguments) throws Exception {

        Command command = null;

        for(int i = 0; i < commandLineArguments.size(); i++) {

            String commandCandidate = commandLineArguments.get(i);
            String commandClassName = getCommandClassName(commandCandidate);

            if (commandClassName != null) {

                //
                // we identified a class file in the class path whose name matches a command class file pattern, so
                // try to load it; since we have our command name, remove it from the top level command line argument
                // list
                //

                commandLineArguments.remove(0);

                Class commandClass;

                try {
                    commandClass = CommandLineApplication.class.getClassLoader().loadClass(commandClassName);
                }
                catch(Exception e) {
                    throw new IllegalStateException("failed to load Command class " + commandClassName);
                }

                try {
                    command = (Command)commandClass.newInstance();
                }
                catch(Exception e) {
                    throw new IllegalStateException("failed to instantiate Command class " + commandClass);
                }

                injectCommandOptions(command, i, commandLineArguments);
            }
        }

        return command;
    }

    /**
     * @return the fully qualified class name of the class that corresponds to the given command, or null if no such
     * class is detected on the classpath.
     */
    static String getCommandClassName(String command) throws Exception {

        //
        // scan the classpath and look for classes implementing the Command interface
        //

        String classPath = System.getProperty(JAVA_CLASS_PATH_SYSTEM_PROPERTY_NAME);

        if (classPath == null) {
            throw new IllegalArgumentException(
                    "no value found for system property '" + JAVA_CLASS_PATH_SYSTEM_PROPERTY_NAME + "'");
        }

        String pathSeparator = System.getProperty(PATH_SEPARATOR_SYSTEM_PROPERTY_NAME);

        if (pathSeparator == null) {
            throw new IllegalArgumentException(
                    "no value found for system property '" + PATH_SEPARATOR_SYSTEM_PROPERTY_NAME + "'");
        }

        List<File> directories = new ArrayList<>();
        List<JarFile> jarFiles = new ArrayList<>();
        for(StringTokenizer st = new StringTokenizer(classPath, pathSeparator); st.hasMoreTokens(); ) {
            String path = st.nextToken();
            log.debug("path: " + path);
            File f = new File(path);
            if (f.isDirectory()) {

                //
                // directory containing classes, possibly commands
                //

                log.debug("directory: " + path);
                directories.add(f);

            }
            else if (f.isFile()) {

                //
                // we assume it's a JAR
                //

                JarFile jarFile = new JarFile(f.getPath());
                log.debug("JAR file: " + jarFile);
                jarFiles.add(jarFile);
            }
        }

        String simpleClassName = commandNameToSimpleClassName(command);
        String fullyQualifiedClassName;

        //
        // by default directories have priority in search
        //

        if (searchOrder == DIRECTORIES_ARE_SEARCHED_FIRST) {

            fullyQualifiedClassName = getFullyQualifiedClassNameFromDirectories(simpleClassName, directories);
            if (fullyQualifiedClassName == null) {
                fullyQualifiedClassName = getFullyQualifiedClassNameFromJars(simpleClassName, jarFiles);
            }
        }
        else if (searchOrder == JARS_ARE_SEARCHED_FIRST) {

            //
            // reverse the search order
            //
            fullyQualifiedClassName = getFullyQualifiedClassNameFromJars(simpleClassName, jarFiles);
            if (fullyQualifiedClassName == null) {
                fullyQualifiedClassName = getFullyQualifiedClassNameFromDirectories(simpleClassName, directories);
            }
        }
        else {
            throw new IllegalArgumentException("invalid search order " + searchOrder);
        }

        return fullyQualifiedClassName;
    }

    static String commandNameToSimpleClassName(String command) {

        if (command == null) {
            throw new IllegalArgumentException("null command name");
        }

        return Character.toUpperCase(command.charAt(0)) + command.substring(1).toLowerCase() + "Command";
    }

    /**
     * @return all files contained by the given directory. The file name is relative to the given directory.
     */
    static List<String> getFileNames(String relativePath, File dir) {

        List<String> result = new ArrayList<>();

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir + " is not a directory");
        }

        File[] content = dir.listFiles();

        if (content != null) {

            for (File f : content) {

                if (f.isFile()) {
                    result.add(relativePath + File.separator + f.getName());
                } else if (f.isDirectory()) {
                    result.addAll(getFileNames(relativePath + File.separator + f.getName(), f));
                }
            }
        }

        return result;
    }

    static List<Option> parseOptions(List<String> commandLineArguments) throws Exception {

        List<Option> result = Collections.emptyList();
        return result;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * @return the fully qualified class name corresponding to the class whose simple name is provided as argument.
     * The search is performed only in the specified directories. May return null if no such class is found.
     */
    private static String getFullyQualifiedClassNameFromDirectories(String simpleClassName, List<File> directories) {

        for(File dir: directories) {

            if (!dir.isDirectory()) {
                continue;
            }

            //noinspection Convert2streamapi
            for(String fileName: getFileNames(dir.getPath(), dir)) {

                if (fileName.endsWith(File.separator + simpleClassName + ".class")) {

                    String className = fileName.substring(dir.getPath().length() + 1);
                    className = className.substring(0, className.length() - ".class".length());
                    className = className.replace(File.separatorChar, '.');
                    return className;
                }
            }
        }

        return null;
    }

    /**
     * @return the fully qualified class name corresponding to the class whose simple name is provided as argument.
     * The search is performed only in the specified JAR files. May return null if no such class is found.
     */
    private static String getFullyQualifiedClassNameFromJars(String simpleClassName, List<JarFile> jarFiles) {

        for(JarFile jarFile: jarFiles) {

            for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String className = entry.getName();
                if (className.endsWith(File.separator + simpleClassName + ".class")) {
                    className = className.substring(0, className.length() - ".class".length());
                    className = className.replace(File.separatorChar, '.');
                    return className;
                }
            }
        }

        return null;
    }

    /**
     * @param commandLineArguments will remove all command line arguments accepted by the command
     */
    private static void injectCommandOptions(Command command, int from, List<String> commandLineArguments)
            throws Exception {

        List<Option> options = OptionParser.parse(from, commandLineArguments);
        command.injectCommandOptions(options);
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}

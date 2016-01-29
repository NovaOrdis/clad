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
import io.novaordis.clad.option.OptionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/16
 */
public class CommandLineApplication {

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

        int exitCode = 0;

        //noinspection finally
        try {
            exitCode = new CommandLineApplication().execute(args);
        }
        finally {
            System.exit(exitCode);
        }
    }

    // Static Package Protected ----------------------------------------------------------------------------------------

    /**
     * The method attempts to locate an ApplicationRuntime implementation on classpath and instantiates it.
     *
     * If no implementation is found, the method returns null.
     *
     * @return the ApplicationRuntime instance or null if no command was identified.
     */
    static ApplicationRuntime identifyRuntime(Configuration configuration) throws Exception {

        ApplicationRuntime runtime = null;

        String applicationName = configuration.getApplicationName();

        if (applicationName == null) {
            throw new UserErrorException("missing application name");
        }

        log.debug("application name: \"" + applicationName + "\"");

        String applicationRuntimeClassName = getFullyQualifiedClassName(applicationName, "ApplicationRuntime");

        if (applicationRuntimeClassName != null) {

            //
            // we identified a class file in the class path whose name matches a application runtime class file pattern,
            // so try to load it
            //
            Class applicationRuntimeClass;

            try {
                applicationRuntimeClass =
                        CommandLineApplication.class.getClassLoader().loadClass(applicationRuntimeClassName);
            }
            catch(Exception e) {
                throw new IllegalStateException(
                        "failed to load ApplicationRuntime class " + applicationRuntimeClassName);
            }

            try {
                runtime = (ApplicationRuntime) applicationRuntimeClass.newInstance();
            }
            catch(Exception e) {
                throw new IllegalStateException(
                        "failed to instantiate ApplicationRuntime class " + applicationRuntimeClass);
            }
        }

        return runtime;
    }

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
    static Command identifyCommand(List<String> commandLineArguments, ConfigurationImpl configuration)
            throws Exception {

        Command command = null;

        for(int i = 0; i < commandLineArguments.size(); i++) {

            String commandCandidateName = commandLineArguments.get(i);
            command = getCommand(commandCandidateName);

            if (command != null) {

                //
                // we identified a class file in the class path whose name matches a command class file pattern, so
                // try to load it
                //

                commandLineArguments.remove(i);

                injectCommandOptionsIntoConfiguration(configuration, i, commandLineArguments);
            }
        }

        return command;
    }

    /**
     * @return a non-initialized Command instance if the corresponding command implementation class was found on the
     * class path and the no-argument constructor instantiation went well.
     */
    static Command getCommand(String commandName) throws Exception {

        String commandClassName = getFullyQualifiedClassName(commandName, "Command");

        if (commandClassName == null) {
            return null;
        }

        //
        // we identified a class file in the class path whose name matches a command class file pattern, so try to load
        // it
        //

        Class commandClass;

        try {
            commandClass = CommandLineApplication.class.getClassLoader().loadClass(commandClassName);
        }
        catch(Exception e) {
            throw new IllegalStateException("failed to load Command class " + commandClassName);
        }

        try {
            return (Command)commandClass.newInstance();
        }
        catch(Exception e) {
            throw new IllegalStateException("failed to instantiate Command class " + commandClass);
        }
    }

    /**
     * @return the fully qualified class name of the class that corresponds to the given prefix (which will be
     * camel-cased) and the given suffix (currently Command or ApplicationRuntime). It will return null if no such
     * class is detected on the classpath.
     */
    static String getFullyQualifiedClassName(String prefix, String suffix) throws Exception {

        //
        // scan the classpath and look for classes named camelCased(prefix) + suffix.
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

        String simpleClassName = toSimpleClassName(prefix, suffix);
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

    static String toSimpleClassName(String prefix, String suffix) {

        if (prefix == null) {
            throw new IllegalArgumentException("null prefix");
        }

        if (suffix == null) {
            throw new IllegalArgumentException("null suffix");
        }


        return Character.toUpperCase(prefix.charAt(0)) + prefix.substring(1).toLowerCase() + suffix;
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

    // Static Private --------------------------------------------------------------------------------------------------

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
    private static void injectCommandOptionsIntoConfiguration(
            ConfigurationImpl configuration, int from, List<String> commandLineArguments) throws Exception {

        List<Option> options = OptionParser.parse(from, commandLineArguments);
        configuration.setCommandOptions(options);
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private OutputStream stdoutOutputStream;
    private OutputStream stderrOutputStream;
    private ConfigurationImpl configuration;

    // Constructors ----------------------------------------------------------------------------------------------------

    CommandLineApplication() {

        this(null);
    }

    CommandLineApplication(OutputStream stderrOutputStream) {

        this(null, stderrOutputStream);
    }

    CommandLineApplication(OutputStream stdoutOutputStream, OutputStream stderrOutputStream) {

        if (stdoutOutputStream == null) {
            stdoutOutputStream = System.out;
        }

        if (stderrOutputStream == null) {
            stderrOutputStream = System.err;
        }

        this.stdoutOutputStream = stdoutOutputStream;
        this.stderrOutputStream = stderrOutputStream;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setStdoutOutputStream(OutputStream outputStream) {

        this.stdoutOutputStream = outputStream;
    }

    public OutputStream getStdoutOutputStream() {
        return stdoutOutputStream;
    }

    public void setStderrOutputStream(OutputStream outputStream) {

        this.stderrOutputStream = outputStream;
    }

    public OutputStream getStderrOutputStream() {
        return stderrOutputStream;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    /**
     * @return the exit code to be returned by the process on exit.
     */
    int execute(String[] args) throws Exception {

        try {

            this.configuration = new ConfigurationImpl();

            //
            // identify and instantiate the runtime
            //

            ApplicationRuntime runtime = identifyRuntime(configuration);

            if (runtime == null) {
                throw new UserErrorException("no application runtime");
            }

            // identify and instantiate the command - the first command line argument that corresponds to a Command
            // implementation

            List<String> commandLineArguments = new ArrayList<>(Arrays.asList(args));

            Command command = identifyCommand(commandLineArguments, configuration);

            log.debug("command: " + command);

            List<Option> globalOptions = OptionParser.parse(0, commandLineArguments);

            log.debug("global options: " + globalOptions);

            // place global options in configuration

            configuration.setGlobalOptions(globalOptions);

            if (command == null) {

                //
                // try to figure out the default command
                //

                String defaultCommandName = runtime.getDefaultCommandName();

                if (defaultCommandName == null) {

                    throw new UserErrorException(
                            "no command specified on command line and no default command was configured");
                }

                // attempt to instantiate the default command and execute it
                command = getCommand(defaultCommandName);

                if (command == null) {

                    throw new UserErrorException("...");
                }
            }

            //
            // handle special situations
            //

            for(Option o: getConfiguration().getGlobalOptions()) {

                if (o instanceof HelpOption) {

                    //
                    // handle help requests
                    //

                    HelpOption helpOption = (HelpOption)o;
                    // inject the current command, if any
                    helpOption.setCommand(command);
                    helpOption.displayHelp(getStdoutOutputStream());
                    return 0;
                }
            }

            //
            // not a special situation, execute the command
            //

            log.debug("initializing the runtime");

            runtime.init(configuration);

            log.debug("runtime initialized");

            command.execute(configuration, runtime);

            log.debug("command successfully executed");

            return 0;
        }
        catch(UserErrorException e) {

            String msg = "[error]: " + e.getMessage() + "\n";
            stderrOutputStream.write(msg.getBytes());
            stderrOutputStream.flush();
            return 1;
        }
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

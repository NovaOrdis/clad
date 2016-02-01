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
import io.novaordis.clad.command.Command;
import io.novaordis.clad.configuration.Configuration;
import io.novaordis.clad.configuration.ConfigurationImpl;
import io.novaordis.clad.option.HelpOption;
import io.novaordis.clad.option.Option;
import io.novaordis.clad.option.OptionParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/22/16
 */
public class CommandLineApplication {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(CommandLineApplication.class);

    // Static ----------------------------------------------------------------------------------------------------------

    public static void main(String[] args) throws Exception {

        int exitCode = 0;

        //noinspection finally
        try {
            exitCode = new CommandLineApplication().run(args);
        }
        catch(Throwable t) {
            log.error("internal error", t);
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

        String normalizedApplicationName = Util.normalizeLabel(applicationName);

        String applicationRuntimeClassName =
                InstanceFactory.getFullyQualifiedClassName(normalizedApplicationName, "ApplicationRuntime");

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
            command = InstanceFactory.getCommand(commandCandidateName);

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
    int run(String[] args) throws Exception {

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

                // a special case is when we execute --help=<command-name>. In this case we don't look for a default
                // command but attempt to execute the in-line help request

                HelpOption helpOption = Configuration.findHelpOption(globalOptions);
                if (helpOption != null) {
                    if (helpOption.getCommand() == null) {

                        // no command detected on the command line and no command name following after --help=
                        // heuristically we assume that the first argument left on the command line is an unknown
                        // command
                        if (!commandLineArguments.isEmpty()) {
                            helpOption.setCommandName(commandLineArguments.get(0));
                        }
                    }
                    helpOption.displayHelp(getStdoutOutputStream());
                    return 0;
                }

                //
                // try to figure out the default command
                //

                String defaultCommandName = runtime.getDefaultCommandName();

                if (defaultCommandName == null) {

                    throw new UserErrorException(
                            "no command specified on command line and no default command was configured");
                }

                // attempt to instantiate the default command and execute it
                command = InstanceFactory.getCommand(defaultCommandName);

                if (command == null) {

                    throw new UserErrorException("no command specified and no default command configured");
                }
            }

            //
            // handle special situations
            //

            HelpOption helpOption = Configuration.findHelpOption(globalOptions);
            if (helpOption != null) {

                // inject the current command, if any - this will fail if the help is already configured with a command

                helpOption.setCommand(command);
                helpOption.displayHelp(getStdoutOutputStream());
                return 0;
            }

            //
            // not a special situation, execute the command
            //

            //
            // for some basic commands (like "version") we don't want to initialize the runtime, because initialization
            // is application-specific and may throw application-specific exception that have nothing to do with
            // those basic commands. All commands "need runtime" by default, with the exception of the basic ones
            //

            if (command.needsRuntime()) {

                log.debug("initializing the runtime");

                runtime.init(configuration);

                log.debug("runtime initialized");
            }

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

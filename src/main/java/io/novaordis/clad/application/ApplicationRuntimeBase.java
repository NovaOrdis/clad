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

package io.novaordis.clad.application;

import io.novaordis.clad.configuration.Configuration;
import io.novaordis.utilities.UserErrorException;
import io.novaordis.utilities.expressions.EncloseableScope;
import io.novaordis.utilities.expressions.Scope;
import io.novaordis.utilities.expressions.ScopeImpl;
import io.novaordis.utilities.expressions.env.OSProcessScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 2/4/16
 */
public abstract class ApplicationRuntimeBase implements ApplicationRuntime {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(ApplicationRuntimeBase.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private OutputStream stdoutOutputStream;
    private OutputStream stderrOutputStream;

    // the scope is configured to resolve environment properties by default
    private EncloseableScope rootScope;

    private Configuration configuration;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected ApplicationRuntimeBase() {

        //
        // start with the default streams, they can be overwritten at any moment
        //

        setStdoutOutputStream(System.out);
        setStderrOutputStream(System.err);

        this.rootScope = new ScopeImpl();

        //
        // this is how we resolve environment variables
        //
        this.rootScope.setParent(new OSProcessScope());

        log.debug(this + " constructed");
    }

    // ApplicationRuntime overrides ------------------------------------------------------------------------------------

    @Override
    public String getName() {

        String simpleName = this.getClass().getSimpleName();

        if (!simpleName.endsWith("ApplicationRuntime")) {
            throw new IllegalStateException(
                    "non-standard application class name " + simpleName + ", we don't know how to handle it");
        }

        return simpleName.substring(0, simpleName.length() - "ApplicationRuntime".length()).toLowerCase();
    }

    @Override
    public String getHelpFilePath() {

        String s = getClass().getName();
        s = s.substring(0, s.lastIndexOf('.'));
        s = s.replace('.', '/');
        return s + "/" + getName() + ".txt";
    }

    @Override
    public void setStdoutOutputStream(OutputStream os) {

        this.stdoutOutputStream = os;
    }

    @Override
    public OutputStream getStdoutOutputStream() {

        return stdoutOutputStream;
    }

    @Override
    public void setStderrOutputStream(OutputStream os) {

        this.stderrOutputStream = os;
    }

    @Override
    public OutputStream getStderrOutputStream() {

        return stderrOutputStream;
    }

    @Override
    public void info(String s) {

        OutputStream stdout = getStdoutOutputStream();

        try {
            stdout.write((s + "\n").getBytes());
        }
        catch(IOException e) {
            System.err.println("internal error: failed to write the application runtime stdout: " + e);
        }
    }

    @Override
    public void warn(String s) {

        OutputStream stdout = getStdoutOutputStream();

        try {
            stdout.write(("[warn]: " + s + "\n").getBytes());
        }
        catch(IOException e) {
            System.err.println("internal error: failed to write the application runtime stdout: " + e);
        }
    }

    @Override
    public void error(String s) {

        OutputStream stderr = getStderrOutputStream();

        try {
            stderr.write(("[error]: " + s + "\n").getBytes());
        }
        catch(IOException e) {
            System.err.println("internal error: failed to write the application runtime stderr: " + e);
        }
    }

    @Override
    public File getCurrentDirectory() {

        return new File(".");
    }

    @Override
    public Scope getRootScope() {

        return rootScope;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * The default implementation installs the given configuration or fails if null.
     *
     * @exception IllegalArgumentException if configuration is null
     */
    @Override
    public void init(Configuration c) throws UserErrorException {

        if (c == null) {

            throw new IllegalArgumentException("null configuration");
        }

        setConfiguration(c);
    }

    @Override
    public Configuration getConfiguration() {

        return configuration;
    }


    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected void setConfiguration(Configuration c) {

        this.configuration = c;
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

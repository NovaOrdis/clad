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
import io.novaordis.clad.option.Option;
import io.novaordis.utilities.NotYetImplementedException;
import io.novaordis.utilities.variable.VariableProvider;

import java.io.File;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/31/16
 */
@SuppressWarnings("unused")
public class ExceptionOnInitializationApplicationRuntime implements ApplicationRuntime {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // ApplicationRuntime implementation -------------------------------------------------------------------------------

    @Override
    public String getName() {
        throw new NotYetImplementedException("getName() NOT YET IMPLEMENTED");
    }

    @Override
    public String getDefaultCommandName() {
        return null;
    }

    @Override
    public String getHelpFilePath() {
        throw new NotYetImplementedException("getHelpFilePath() NOT YET IMPLEMENTED");
    }

    @Override
    public Set<Option> requiredGlobalOptions() {
        return Collections.emptySet();
    }

    @Override
    public Set<Option> optionalGlobalOptions() {
        return Collections.emptySet();
    }

    @Override
    public void init(Configuration configuration) throws Exception {

        throw new SyntheticException("SYNTHETIC");
    }

    @Override
    public Configuration getConfiguration() {

        throw new NotYetImplementedException("getConfiguration() NOT YET IMPLEMENTED");
    }

    @Override
    public void setStdoutOutputStream(OutputStream outputStream) {

        //
        // this needs to simulate working correctly
        //

        // noop
    }

    @Override
    public OutputStream getStdoutOutputStream() {
        throw new NotYetImplementedException("getStdoutOutputStream() NOT YET IMPLEMENTED");
    }

    @Override
    public void setStderrOutputStream(OutputStream outputStream) {

        //
        // this needs to simulate working correctly
        //

        // noop
    }

    @Override
    public OutputStream getStderrOutputStream() {
        throw new NotYetImplementedException("getStderrOutputStream() NOT YET IMPLEMENTED");
    }

    @Override
    public void info(String s) {
        throw new NotYetImplementedException("info() NOT YET IMPLEMENTED");
    }

    @Override
    public void warn(String s) {
        throw new NotYetImplementedException("warn() NOT YET IMPLEMENTED");
    }

    @Override
    public void error(String s) {
        throw new NotYetImplementedException("error() NOT YET IMPLEMENTED");
    }

    @Override
    public File getCurrentDirectory() {
        throw new NotYetImplementedException("getCurrentDirectory() NOT YET IMPLEMENTED");
    }

    @Override
    public String resolveVariables(String s) {
        throw new RuntimeException("resolveVariables() NOT YET IMPLEMENTED");
    }

    @Override
    public String getVariableValue(String variableName) {
        throw new RuntimeException("getValue() NOT YET IMPLEMENTED");
    }

    @Override
    public String setVariableValue(String variableName, String variableValue) {
        throw new RuntimeException("setValue() NOT YET IMPLEMENTED");
    }

    @Override
    public VariableProvider getVariableProviderParent() {
        throw new RuntimeException("getVariableProviderParent() NOT YET IMPLEMENTED");
    }

    @Override
    public void setVariableProviderParent(VariableProvider parent) {
        throw new RuntimeException("setVariableProviderParent() NOT YET IMPLEMENTED");
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

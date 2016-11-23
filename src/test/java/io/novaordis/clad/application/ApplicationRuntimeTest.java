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

import io.novaordis.clad.MockOutputStream;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/8/16
 */
public abstract class ApplicationRuntimeTest {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    @Test
    public void stdoutHandling() throws Exception {

        ApplicationRuntime runtime = getApplicationRuntimeToTest();

        MockOutputStream mos = new MockOutputStream();

        runtime.setStdoutOutputStream(mos);

        assertEquals(mos, runtime.getStdoutOutputStream());
    }

    @Test
    public void stderrHandling() throws Exception {

        ApplicationRuntime runtime = getApplicationRuntimeToTest();

        MockOutputStream mos = new MockOutputStream();

        runtime.setStderrOutputStream(mos);

        assertEquals(mos, runtime.getStderrOutputStream());
    }

    /**
     * Tests ApplicationRuntimeBase behavior.
     */
    @Test
    public void info() throws Exception {

        ApplicationRuntime runtime = getApplicationRuntimeToTest();

        MockOutputStream stdout = new MockOutputStream();
        runtime.setStdoutOutputStream(stdout);

        MockOutputStream stderr = new MockOutputStream();
        runtime.setStderrOutputStream(stderr);

        runtime.info("test");

        assertEquals("test\n", stdout.getWrittenString());
        assertEquals(0, stderr.getWrittenBytes().length);
    }

    /**
     * Tests ApplicationRuntimeBase behavior.
     */
    @Test
    public void warn() throws Exception {

        ApplicationRuntime runtime = getApplicationRuntimeToTest();

        MockOutputStream stdout = new MockOutputStream();
        runtime.setStdoutOutputStream(stdout);

        MockOutputStream stderr = new MockOutputStream();
        runtime.setStderrOutputStream(stderr);

        runtime.warn("test");

        assertEquals("[warn]: test\n", stdout.getWrittenString());
        assertEquals(0, stderr.getWrittenBytes().length);
    }

    /**
     * Tests ApplicationRuntimeBase behavior.
     */
    @Test
    public void error() throws Exception {

        ApplicationRuntime runtime = getApplicationRuntimeToTest();

        MockOutputStream stdout = new MockOutputStream();
        runtime.setStdoutOutputStream(stdout);

        MockOutputStream stderr = new MockOutputStream();
        runtime.setStderrOutputStream(stderr);

        runtime.error("test");

        assertEquals(0, stdout.getWrittenBytes().length);
        assertEquals("[error]: test\n", stderr.getWrittenString());
    }

    // current directory -----------------------------------------------------------------------------------------------

    /**
     * Default ApplicationRuntimeBase behavior.
     */
    @Test
    public void getCurrentDirectory() throws Exception {

        ApplicationRuntime runtime = getApplicationRuntimeToTest();

        File crtDir = runtime.getCurrentDirectory();

        assertEquals(new File("."), crtDir);
    }

    // variable replacement --------------------------------------------------------------------------------------------

    @Test
    public void resolveVariables_Null() throws Exception {

        ApplicationRuntime r = getApplicationRuntimeToTest();

        assertNull(r.resolveVariables(null));
     }

    @Test
    public void resolveVariables_NoVariables() throws Exception {

        ApplicationRuntime r = getApplicationRuntimeToTest();

        String orig = "this is a \"string\" that contains 'no' variable";

        String s = r.resolveVariables(orig);

        assertEquals(orig, s);
    }

    @Test
    public void resolveVariables_VariableDoesNotExistInTheRuntime() throws Exception {

        ApplicationRuntime r = getApplicationRuntimeToTest();

        String orig = "I am sure ${there.is.no.such.variable}";

        String s = r.resolveVariables(orig);

        assertEquals(orig, s);
    }

    @Test
    public void resolveVariables_VariableExistsInTheRuntime() throws Exception {

        ApplicationRuntime r = getApplicationRuntimeToTest();

        r.setValue("this.is.a.variable", "d");

        String orig = "a b c ${this.is.a.variable} e";

        String s = r.resolveVariables(orig);

        assertEquals("a b c d e", s);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    protected abstract ApplicationRuntime getApplicationRuntimeToTest();

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

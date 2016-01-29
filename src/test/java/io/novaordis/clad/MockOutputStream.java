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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/28/16
 */
public class MockOutputStream extends OutputStream {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private ByteArrayOutputStream baos;

    // Constructors ----------------------------------------------------------------------------------------------------

    public MockOutputStream() {
        this.baos = new ByteArrayOutputStream();
    }

    // OutputStream overrides ------------------------------------------------------------------------------------------

    @Override
    public void write(int b) throws IOException {

        baos.write(b);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public byte[] getWrittenBytes() {

        return baos.toByteArray();
    }

    public String getWrittenString() {

        return new String(getWrittenBytes());
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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

import io.novaordis.clad.ApplicationRuntime;
import io.novaordis.clad.Command;
import io.novaordis.clad.Configuration;
import io.novaordis.clad.UserErrorException;
import io.novaordis.utilities.VersionUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/27/16
 */
public class VersionCommand implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(VersionCommand.class);

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private OutputStream os;

    // Constructors ----------------------------------------------------------------------------------------------------

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public void execute(Configuration configuration, ApplicationRuntime runtime) throws UserErrorException {

        String version = VersionUtilities.getVersion();
        String releaseDate = VersionUtilities.getReleaseDate();

        StringBuilder sb = new StringBuilder();
        sb.append("version ").append((version == null ? "N/A" : version)).append("\n");
        sb.append("release date ").append((releaseDate == null ? "N/A" : releaseDate)).append("\n");

        if (os == null) {
            System.out.print(sb);
        }
        else {
            try {
                os.write(sb.toString().getBytes());
                os.flush();
            }
            catch(IOException e) {

                log.error("failed to write to output stream", e);
            }
        }
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public void setOutputStream(OutputStream os) {
        this.os = os;
    }

    public OutputStream getOutputStream() {
        return os;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

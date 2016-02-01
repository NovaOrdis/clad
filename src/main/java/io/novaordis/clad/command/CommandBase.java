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

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/29/16
 */
public abstract class CommandBase implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public int compareTo(Command o) {

        if (o == null) {
            throw new NullPointerException();
        }
        return getName().compareTo(o.getName());
    }

    @Override
    public String getName() {

        String s = getClass().getSimpleName();
        return s.replaceAll("Command", "").toLowerCase();
    }

    @Override
    public String getHelpFilePath() {

        String s = getClass().getName();
        s = s.substring(0, s.lastIndexOf('.'));
        s = s.replace('.', '/');
        return s + "/" + getName() + ".txt";
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

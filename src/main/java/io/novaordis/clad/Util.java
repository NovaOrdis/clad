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

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/31/16
 */
public class Util {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static Attributes -----------------------------------------------------------------------------------------------

    public static boolean normalizeLabelInvoked = false;

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Normalizes an external label (command name, application name) by eliminating dashes, camel-casing, etc.
     * For example, "app-one" becomes "appOne" after normalization.
     */
    public static String normalizeLabel(String name) {

        normalizeLabelInvoked = true;

        String result = "";
        for(int i = 0; i < name.length(); i ++) {

            if (i == 0) {

                result += Character.toUpperCase(name.charAt(i));
            }
            else if (name.charAt(i) == '-') {

                if (i < name.length() - 1) {
                    result += Character.toUpperCase(name.charAt(++i));
                }
            }
            else {
                result += name.charAt(i);
            }
        }

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    private Util() {
    }

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

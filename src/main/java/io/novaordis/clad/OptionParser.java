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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class OptionParser {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * @param commandLineArguments will remove all command line arguments that were parsed into Options
     */
    public static List<Option> parse(int from, List<String> commandLineArguments) throws UserErrorException {

        List<Option> result = new ArrayList<>();

        String current;

        for(int i = from; i < commandLineArguments.size(); i++) {

            current = commandLineArguments.get(i);

            if (current.startsWith("--")) {
                throw new RuntimeException("NOT YET IMPLEMENTED");
            }
            else if (current.startsWith("-")) {

                if (current.length() == 1) {
                    // "-" - currently we have no use for it, advertise it as a user error
                    throw new UserErrorException("invalid option \"-\"");
                }

                //
                // short option - we only use the character following '-' and we ignore the rest
                //

                commandLineArguments.remove(current);

                char shortLiteral = current.charAt(1);

                if (commandLineArguments.isEmpty() || commandLineArguments.get(i).startsWith("-")) {

                    // boolean option
                    result.add(new BooleanOption(shortLiteral));
                }
                else {
                    String valueAsString = commandLineArguments.get(0);
                    Object value = typeHeuristics(valueAsString);
                    Option option;
                    if (value instanceof String) {
                        option = new StringOption(shortLiteral);
                        ((StringOption)option).setValue((String)value);

                    }
                    else if (value instanceof Long) {
                        throw new RuntimeException("NOT YET IMPLEMENTED " + value);

                    }
                    else if (value instanceof Double) {
                        throw new RuntimeException("NOT YET IMPLEMENTED " + value);
                    }
                    else {
                        throw new RuntimeException("NOT YET IMPLEMENTED " + value);
                    }

                    result.add(option);
                    commandLineArguments.remove(i);
                }
            }
            else {
                throw new RuntimeException("NOT YET IMPLEMENTED");
            }
        }

        return result;
    }

    public static Object typeHeuristics(String value) {

        if (value == null) {
            return null;
        }

        //
        // attempt to convert to numeric value
        //

        try {

            return Long.parseLong(value);
        }
        catch(Exception e) {

            //
            // not a long, that's OK
            //

            try {

                return Double.parseDouble(value);

            }
            catch (Exception e2) {

                //
                // not a double, that's OK
                //

                String lc = value.toLowerCase();

                if ("true".equals(lc)) {

                    return Boolean.TRUE;
                }

                if ("false".equals(lc)) {

                    return Boolean.FALSE;
                }

                return value;
            }
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

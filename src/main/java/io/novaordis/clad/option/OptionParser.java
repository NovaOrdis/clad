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

package io.novaordis.clad.option;

import io.novaordis.clad.command.Command;
import io.novaordis.clad.UserErrorException;
import io.novaordis.clad.InstanceFactory;

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
    public static List<Option> parse(int from, List<String> commandLineArguments) throws Exception {

        //
        // pre-parse to handle single quotes and double quotes
        //
        handleQuotes(from, commandLineArguments);

        List<Option> options = new ArrayList<>();

        String current;

        for(int i = from; i < commandLineArguments.size(); i++) {

            current = commandLineArguments.get(i);

            if (handledHelpOption(i, commandLineArguments, options)) {

                //
                // handled as help option
                //

                //noinspection UnnecessaryContinue
                continue;
            }
            else if (current.startsWith("--")) {

                String longLiteralOptionString = commandLineArguments.remove(i--);
                Option option = parseLongLiteralOption(longLiteralOptionString);
                options.add(option);
            }
            else if (current.startsWith("-")) {

                if (current.length() == 1) {
                    // "-" - currently we have no use for it, advertise it as a user error
                    throw new UserErrorException("invalid option: '-'");
                }

                //
                // short option - we only use the character following '-' and we ignore the rest
                //

                commandLineArguments.remove(current);

                char shortLiteral = current.charAt(1);

                if (commandLineArguments.isEmpty() || commandLineArguments.get(i).startsWith("-")) {

                    // boolean option
                    options.add(new BooleanOption(shortLiteral));
                }
                else {
                    String valueAsString = commandLineArguments.get(i);
                    Object value = typeHeuristics(valueAsString);
                    Option option;
                    if (value instanceof String) {
                        option = new StringOption(shortLiteral);
                        ((StringOption)option).setValue((String)value);

                    }
                    else if (value instanceof Long) {
                        option = new LongOption(shortLiteral);
                        ((LongOption)option).setValue((Long)value);

                    }
                    else if (value instanceof Double) {
                        option = new DoubleOption(shortLiteral);
                        ((DoubleOption)option).setValue((Double)value);
                    }
                    else {
                        throw new RuntimeException("NOT YET IMPLEMENTED " + value);
                    }

                    options.add(option);
                    commandLineArguments.remove(i--);
                }
            }
            else {
                throw new UserErrorException("unknown option: '" + current + "'");
            }
        }

        return options;
    }

    /**
     * Coalesces strings between single and double quotes in place withing the argument list. Works by side-effect.
     * @param from - only start from 'from', leave the first arguments untouched.
     */
    public static void handleQuotes(int from, List<String> commandLineArguments) throws UserErrorException {

        StringBuilder doubleQuoted = null;
        StringBuilder singleQuoted = null;
        int index = -1;
        int toRemoveCount = 0;

        for(int i = from; i < commandLineArguments.size(); i++) {

            String current = commandLineArguments.get(i);

            if (current.startsWith("\"") || current.startsWith("'")) {

                boolean doubleQuote = current.startsWith("\"");
                boolean singleQuote = current.startsWith("'");

                if (doubleQuote && doubleQuoted != null) {
                    String beginning = doubleQuoted.toString();
                    if (beginning.indexOf(' ') != -1) {
                        beginning = beginning.substring(0, beginning.indexOf(' '));
                    }
                    throw new UserErrorException("unbalanced double quotes: \"" + beginning + " ... " + current);
                }

                if (singleQuote && singleQuoted != null) {
                    String beginning = singleQuoted.toString();
                    if (beginning.indexOf(' ') != -1) {
                        beginning = beginning.substring(0, beginning.indexOf(' '));
                    }
                    throw new UserErrorException("unbalanced single quotes: '" + beginning + " ... " + current);
                }

                current = current.substring(1);
                doubleQuoted = doubleQuote ? new StringBuilder(current) : null;
                singleQuoted = singleQuote ? new StringBuilder(current) : null;
                index = i;

            }
            else if (doubleQuoted != null || singleQuoted != null) {

                toRemoveCount ++;

                if ((current.endsWith("\"") && current.charAt(current.length() - 2) != '\\') ||
                    (current.endsWith("'") && current.charAt(current.length() - 2) != '\\'))
                {
                    //
                    // end quote (but NOT escaped quote)
                    //

                    boolean doubleQuote = current.endsWith("\"");

                    current = current.substring(0, current.length() - 1);

                    if (doubleQuote) {
                        assert doubleQuoted != null;
                        doubleQuoted.append(" ").append(current);
                    }
                    else {
                        assert singleQuoted != null;
                        singleQuoted.append(" ").append(current);
                    }

                    //
                    // replace in place
                    //

                    commandLineArguments.set(index, doubleQuote ? doubleQuoted.toString() : singleQuoted.toString());

                    //
                    // remove components
                    //

                    for(int j = 0; j < toRemoveCount; j ++) {
                        commandLineArguments.remove(index + 1);
                        i--;
                    }

                    toRemoveCount = 0;

                    if (doubleQuote) {
                        doubleQuoted = null;
                    }
                    else {
                        singleQuoted = null;
                    }
                }
                else if (doubleQuoted != null) {
                    doubleQuoted.append(" ").append(current);
                }
                else //noinspection ConstantConditions
                    if (singleQuoted != null) {
                        singleQuoted.append(" ").append(current);
                    }
            }
        }

        //
        // at this point the commandLineArguments list may contain escaped quotes - replace them with simple quotes
        //

        for(int i = from; i < commandLineArguments.size(); i++) {

            String arg = commandLineArguments.get(i);
            arg = arg.replaceAll("\\\\", "");
            commandLineArguments.set(i, arg);
        }

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

    public static Option parseLongLiteralOption(String longLiteralOptionString) throws UserErrorException {

        if (longLiteralOptionString == null) {
            throw new IllegalArgumentException("null argument");
        }

        String original = longLiteralOptionString;

        if (!longLiteralOptionString.startsWith("--")) {

            throw new IllegalArgumentException("argument does not start with '--': " + original);
        }

        longLiteralOptionString = longLiteralOptionString.substring(2);

        int i = longLiteralOptionString.indexOf('=');

        if (i == -1) {
            throw new UserErrorException("--" + longLiteralOptionString + " option does not contain '='");
        }

        String optionName = longLiteralOptionString.substring(0, i);
        String valueAsString = longLiteralOptionString.substring(i + 1);

        Object o = typeHeuristics(valueAsString);

        if (o instanceof Long) {
            LongOption option = new LongOption(optionName);
            option.setValue((Long)o);
            return option;
        }
        else if (o instanceof Double) {
            DoubleOption option = new DoubleOption(optionName);
            option.setValue((Double)o);
            return option;
        }
        else if (o instanceof Boolean) {
            BooleanOption option = new BooleanOption(optionName);
            option.setValue((Boolean)o);
            return option;
        }
        else {
            StringOption option = new StringOption(optionName);
            option.setValue((String)o);
            return option;
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Static Package protected ----------------------------------------------------------------------------------------

    /**
     * @return true if it detected and successfully handled a help option. If the option is detected, both the
     * command line argument list and the option list are modified by side effect: the corresponding argument
     * (arguments) are removed and the HelpOption instance is added to the options list.
     */
    static boolean handledHelpOption(int index, List<String> commandLineArguments, List<Option> options)
            throws Exception {

        int i = index;
        for(; i < commandLineArguments.size(); i ++) {

            String current = commandLineArguments.get(i);

            if (("--" + HelpOption.LONG_LITERAL).equals(current) ||
                    HelpOption.LONG_LITERAL.equals(current) ||
                    ("-" + HelpOption.SHORT_LITERAL).equals(current))  {

                commandLineArguments.remove(i);
                options.add(new HelpOption());
                return true;
            }
            else if (current.startsWith("--" + HelpOption.LONG_LITERAL + "=")) {
                String commandName = current.substring(("--" + HelpOption.LONG_LITERAL + "=").length());

                Command command = InstanceFactory.getCommand(commandName);

                commandLineArguments.remove(i);

                if (command == null) {
                    throw new UserErrorException("unknown command: '" + commandName + "'");
                }

                HelpOption option = new HelpOption();
                option.setCommand(command);
                options.add(option);
                return true;
            }
        }

        return false;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

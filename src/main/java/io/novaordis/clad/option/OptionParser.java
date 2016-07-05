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

import io.novaordis.clad.InstanceFactory;
import io.novaordis.clad.UserErrorException;
import io.novaordis.clad.command.Command;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class OptionParser {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = Logger.getLogger(OptionParser.class);

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * Process the command line arguments looking for known (required and optional) options. The method does not
     * check whether the required options are present, that check belongs in the application or command.
     *
     * @param commandLineArguments will remove all command line arguments that were parsed into Options
     */
    public static List<Option> parse(int from, List<String> commandLineArguments,
                                     Set<Option> required, Set<Option> optional) throws Exception {

        //
        // pre-parse to handle single quotes and double quotes
        //

        coalesceQuotedSections(from, commandLineArguments);

        List<Option> options = new ArrayList<>();

        String current;

        for(int i = from; i < commandLineArguments.size(); i++) {

            current = commandLineArguments.get(i);

            if (("--" + VerboseOption.LONG_LITERAL).equals(current) ||
                    ("-" + VerboseOption.SHORT_LITERAL).equals(current)) {

                VerboseOption verboseOption = new VerboseOption();
                if(required.contains(verboseOption) || optional.contains(verboseOption)) {
                    commandLineArguments.remove(i--);
                    options.add(verboseOption);
                }
            }
            else if (handledHelpOption(i, commandLineArguments, options)) {

                //
                // handled as help option - this is handled out-of-band and the list of required and optional options
                // is not consulted
                //

                //noinspection UnnecessaryContinue
                continue;
            }
            else if (current.startsWith("--")) {

                String longLiteralOptionString = commandLineArguments.get(i);
                Option option = parseLongLiteralOption(longLiteralOptionString);

                if (isRequiredOption(option, required) || isOptionalOption(option, optional)) {
                    commandLineArguments.remove(i--);
                    options.add(option);
                    copyLiterals(option, required, optional);
                }
            }
            else if (current.startsWith("-")) {

                if (current.length() == 1) {
                    // "-" - currently we have no use for it, advertise it as a user error
                    throw new UserErrorException("invalid option: '-'");
                }

                //
                // short option candidate - we only use the character following '-' and we ignore the rest
                //

                char shortLiteral = current.charAt(1);

                Option candidateOption;

                if (i == (commandLineArguments.size() - 1) || commandLineArguments.get(i + 1).startsWith("-")) {

                    // boolean option
                    candidateOption = new BooleanOption(shortLiteral);
                }
                else {

                    String valueAsString = commandLineArguments.get(i + 1);
                    Object value = typeHeuristics(valueAsString);
                    if (value instanceof String) {
                        candidateOption = new StringOption(shortLiteral);
                        ((StringOption)candidateOption).setValue((String)value);

                    }
                    else if (value instanceof Long) {
                        candidateOption = new LongOption(shortLiteral);
                        ((LongOption)candidateOption).setValue((Long)value);

                    }
                    else if (value instanceof Double) {
                        candidateOption = new DoubleOption(shortLiteral);
                        ((DoubleOption)candidateOption).setValue((Double)value);
                    }
                    else {
                        throw new RuntimeException("NOT YET IMPLEMENTED " + value);
                    }
                }

                if (required.contains(candidateOption) || optional.contains(candidateOption)) {

                    //
                    // only add if we know about it
                    //

                    options.add(candidateOption);
                    copyLiterals(candidateOption, required, optional);
                    if (candidateOption instanceof BooleanOption) {
                        // remove one
                        commandLineArguments.remove(i);
                    }
                    else {
                        // remove two
                        commandLineArguments.remove(i);
                        commandLineArguments.remove(i--);
                    }
                }
            }
            else {

                log.debug("unknown option \"" + current + "\", ignoring it");
            }
        }

        //
        // Do not check for required options yet, that check belongs in the command or application.
        //

        return options;
    }

    /**
     * @return true if the option is a required option (is either in the required set, or is equivalent with an
     * option in the required set)
     */
    public static boolean isRequiredOption(Option option, Set<Option> required) {

        for(Option o: required) {
            if (o.equals(option) || o.isEquivalentWith(option)) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return true if the option is a optional option (is either in the optional set, or is equivalent with an
     * option in the optional set)
     */
    public static boolean isOptionalOption(Option option, Set<Option> optional) {

        for(Option o: optional) {
            if (o.equals(option) || o.isEquivalentWith(option)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Coalesces strings between single and double quotes in place withing the argument list. Works by side-effect.
     * @param from - only start from 'from', leave the first arguments untouched.
     */
    public static void coalesceQuotedSections(int from, List<String> commandLineArguments) throws UserErrorException {

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
        // first try timestamps
        //

        Date date = TimestampOption.parseValue(value);
        if (date != null) {
            return date;
        }

        //
        // attempt to convert to numeric value
        //

        try {

            return Long.parseLong(value);

        }
        catch (Exception e) {

            // ignore, keep trying
        }

        //
        // not a long, that's OK
        //

        try {

            return Double.parseDouble(value);

        }
        catch (Exception e) {

            // ignore, keep trying
        }

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

        //
        // string
        //

        return value;
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

        String optionName, valueAsString;

        int i = longLiteralOptionString.indexOf('=');

        if (i == -1) {

            //
            // we interpret this as a "true" boolean option
            //
            optionName = longLiteralOptionString;
            valueAsString = "true";
        }
        else {
            optionName = longLiteralOptionString.substring(0, i);
            valueAsString = longLiteralOptionString.substring(i + 1);
        }

        Object o = typeHeuristics(valueAsString);

        if (o instanceof Date) {

            TimestampOption option = new TimestampOption(optionName);
            option.setValue(o);
            return option;
        }

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

// Private Static --------------------------------------------------------------------------------------------------

    private static void copyLiterals(Option option, Set<Option> required, Set<Option> optional) {

        for(Option o: required) {
            if (o.equals(option)) {
                Character shortLiteral = o.getShortLiteral();
                if (shortLiteral != null) {
                    option.setShortLiteral(shortLiteral);
                }
                String longLiteral = o.getLongLiteral();
                if (longLiteral != null) {
                    option.setLongLiteral(longLiteral);
                }
                break;
            }
        }

        for(Option o: optional) {
            if (o.equals(option)) {
                Character shortLiteral = o.getShortLiteral();
                if (shortLiteral != null) {
                    option.setShortLiteral(shortLiteral);
                }
                String longLiteral = o.getLongLiteral();
                if (longLiteral != null) {
                    option.setLongLiteral(longLiteral);
                }
                break;
            }
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}

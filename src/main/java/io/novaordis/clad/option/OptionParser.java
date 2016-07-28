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
import io.novaordis.utilities.UserErrorException;
import io.novaordis.clad.command.Command;
import org.apache.log4j.Logger;

import java.util.ArrayList;
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
     * check whether the *required* options are present, that check belongs in the application or command.
     *
     * The method applies type heuristics. The logic assumes that timestamp values (and timestamp values only) may or
     * may be not enclosed in quotes, so, for example, both --from=07/23/16 14:00:00 and --from="=07/23/16 14:00:00"
     * should be valid and recognized as valid timestamp values by the parser.
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
                Option option = parseLongLiteralOption(longLiteralOptionString, commandLineArguments, i + 1);

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
                    Object value = typeHeuristics(valueAsString, null);
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

            int doubleQuoteIndex, singleQuoteIndex = -1;

            if (((doubleQuoteIndex = current.indexOf("\"")) != -1 &&
                    doubleQuoteIndex < current.length() - 1 &&
                    (doubleQuoteIndex == 0 || current.charAt(doubleQuoteIndex -1) != '\\')) ||
                    ((singleQuoteIndex = current.indexOf("'")) != -1 &&
                            singleQuoteIndex < current.length() - 1) &&
                            (singleQuoteIndex == 0 || current.charAt(singleQuoteIndex -1) != '\\')) {

                boolean doubleQuote = doubleQuoteIndex != -1;
                boolean singleQuote = singleQuoteIndex != -1;

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

                int separatorIndex = singleQuoteIndex == -1 ? doubleQuoteIndex : singleQuoteIndex;

                current = current.substring(0, separatorIndex) + current.substring(separatorIndex + 1);
                doubleQuoted = doubleQuote ? new StringBuilder(current) : null;
                singleQuoted = singleQuote ? new StringBuilder(current) : null;
                index = i;

            }
            else if (doubleQuoted != null || singleQuoted != null) {

                toRemoveCount ++;

                if ((current.endsWith("\"") && current.length() > 1 && current.charAt(current.length() - 2) != '\\') ||
                        (current.endsWith("'") && current.length() > 1 && current.charAt(current.length() - 2) != '\\'))
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

    /**
     * The method applies type heuristics by attempting to figure out the correct option type from the string
     * representation. The logic assumes that timestamp values (and timestamp values only) may or
     * may be not enclosed in quotes, so, for example, both --from=07/23/16 14:00:00 and --from="=07/23/16 14:00:00"
     * should be valid and recognized as valid timestamp values by the parser.

     *
     * @param nextCommandLineSections needed because we can specify timestamps without quotes. If used, the used
     *                                sections will be removed from the array. The method must be prepared to handle
     *                                the cases when the list is null or empty.
     */
    public static Object typeHeuristics(String value, List<String> nextCommandLineSections) {

        if (value == null) {
            return null;
        }

        //
        // first try timestamps
        //

        if (TimestampOption.isTimestampOptionValue(value)) {
            return value;
        }

        //
        // timestamp is a special case in that we allow space separated sections of the timestamp to be specified
        // on command line without quote enclosure
        //

        if (nextCommandLineSections != null && nextCommandLineSections.size() > 0) {

            String timestampCandidateValue = value + " " + nextCommandLineSections.get(0);
            if (TimestampOption.isTimestampOptionValue(timestampCandidateValue)) {

                nextCommandLineSections.remove(0);
                return timestampCandidateValue;
            }
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

    /**
     * The method applies type heuristics. The logic assumes that timestamp values (and timestamp values only) may or
     * may be not enclosed in quotes, so, for example, both --from=07/23/16 14:00:00 and --from="=07/23/16 14:00:00"
     * should be valid and recognized as valid timestamp values by the parser.
     *
     * @param commandLineArguments the current command line arguments list. We need it in order to handle space-separate
     *                             options that are not enclosed by quotes.
     * @param nextArgumentIndex - position in the command line argument list of the next argument that can be used
     *                          when looking for timestamps. The method must be prepared to handle the case when the
     *                          index is out out bounds, in which case it should be ignored.
     */
    public static Option parseLongLiteralOption(
            String longLiteralOptionString, List<String> commandLineArguments, int nextArgumentIndex)
            throws UserErrorException {

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

        List<String> theRestOfTheCommandLineArgs = new ArrayList<>();
        if (commandLineArguments != null) {
            for (int k = nextArgumentIndex; k < commandLineArguments.size(); k++) {
                theRestOfTheCommandLineArgs.add(commandLineArguments.get(k));
            }
        }
        int remainingArgCount = theRestOfTheCommandLineArgs.size();

        Object o = typeHeuristics(valueAsString, theRestOfTheCommandLineArgs);

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
        else if (o instanceof String) {

            if (TimestampOption.isTimestampOptionValue((String)o)) {

                TimestampOption option = new TimestampOption(optionName, (String) o);

                //
                // timestamp is a special case where heuristics may use the next command line argument (we allow timestamps
                // to be specified without quotes), so if this is the case, adjust the command line accordingly
                //
                int differenceInArgumentCount = remainingArgCount - theRestOfTheCommandLineArgs.size();
                if (differenceInArgumentCount > 1) {
                    throw new RuntimeException(
                            "not prepared to handle the case when we use more than one trailing arguments for the timestamp");
                }
                if (commandLineArguments != null && differenceInArgumentCount == 1) {
                    //
                    // we used arguments for the timestamp
                    //
                    commandLineArguments.remove(nextArgumentIndex);
                }

                return option;
            }
            else {

                StringOption option = new StringOption(optionName);
                option.setValue((String)o);
                return option;
            }
        }
        else {

            throw new IllegalArgumentException("options of type " + o + " not supported");
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

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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Used to declare timestamp options. A timestamp option represents a point in time, with millisecond precision.
 * The option instance does not store the parsed numeric timestamp value, just the string representation. However,
 * the string value is parsed during the instance construction to detect invalid formats. The current implementation
 * relies on a built-in default timestamp format, but it could be improved in the future to allow for a pluggable
 * format.
 *
 * The default built-in full timestamp format is:
 *
 * MM/dd/yy HH:mm:ss
 *
 * The format does not provide for an explicitly declared timezone, and the users of the TimestampOption instances must
 * account for that and adjust the values in case the events' timestamp are declared with a specific timezone offset.
 *
 * A "relative" format that can be used to declare timestamps within the boundaries of the same day is:
 *
 * HH:mm:ss
 *
 * No quotation marks are necessary around the timestamp string, the parser knows how to handle the space between the
 * date section and the time section.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TimestampOption extends OptionBase {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String DEFAULT_FORMAT_AS_STRING = "MM/dd/yy HH:mm:ss";

    public static final DateFormat DEFAULT_FULL_FORMAT = new SimpleDateFormat(DEFAULT_FORMAT_AS_STRING);

    // Static ----------------------------------------------------------------------------------------------------------

    public static boolean isTimestampOptionValue(String value) {

        try {
            new TimestampOption(null, value);
            return true;
        }
        catch(IllegalArgumentException e) {
            return false;
        }
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private String value;
    private boolean relative;
    private DateFormat fullFormat;
    private DateFormat relativeFormat;

    // Constructors ----------------------------------------------------------------------------------------------------

    public TimestampOption(String longLiteral) {
        this(null, longLiteral, null);
    }

    /**
     * @param longLiteral the literal (without '--')
     *
     * @exception IllegalArgumentException on invalid value.
     */
    public TimestampOption(String longLiteral, String value) {

        this(null, longLiteral, value);
    }

    /**
     * @param longLiteral the literal (without '--')
     *
     * @param value - null option is acceptable, it can be installed later with setValue().
     *
     * @exception IllegalArgumentException on invalid value.
     */
    public TimestampOption(Character shortLiteral, String longLiteral, String value) {

        super(shortLiteral, longLiteral);

        this.fullFormat = DEFAULT_FULL_FORMAT;

        this.relativeFormat =
                new SimpleDateFormat(DEFAULT_FORMAT_AS_STRING.substring(DEFAULT_FORMAT_AS_STRING.indexOf(' ') + 1));

        setValue(value);
    }

    // OptionBase overrides --------------------------------------------------------------------------------------------

    @Override
    public void setValue(Object o) {

        if (o == null) {

            value = null;
            relative = false;
            return;
        }

        if (!(o instanceof String)) {
            throw new IllegalArgumentException("value is not a String");
        }

        Object[] result = validateValue((String)o);

        this.value = (String)result[0];
        this.relative = (Boolean)result[1];
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {

        return super.toString(value == null ?  "" : value);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public DateFormat getFullFormat() {

        return fullFormat;
    }

    public DateFormat getRelativeFormat() {

        return relativeFormat;
    }

    /**
     * @return true if the timestamp is relative to the beginning of the day ("14:01:02") or false if the timestamp
     * is specified in full, including the day ("07/25/16 14:01:02").
     */
    public boolean isRelative() {

        return relative;
    }

    public String getString() {

        return value;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    /**
     * @throws IllegalArgumentException on invalid value.
     *
     * @return an array that has on the first position the validated value as string - may be adjusted, trimmed, etc.
     * and on the second the boolean value of "is relative"
     */
    private Object[] validateValue(String value) throws IllegalArgumentException {

        Object[] result = new Object[2];

        try {

            getFullFormat().parse(value);

            result[0] = value;
            result[1] = false;
            return result;

        }
        catch(ParseException e) {
            // ignore, try next
        }

        try {

            getRelativeFormat().parse(value);

            result[0] = value;
            result[1] = true;
            return result;

        }
        catch(ParseException e) {

            throw new IllegalArgumentException("\"" + value + "\" does not match neither format (full or relative)");
        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------




}

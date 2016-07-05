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
import java.util.Date;

/**
 * Used to declare timestamp options. A timestamp option represents a point in time, with millisecond precision.
 * Various formats may be used to declared it. The default full format is:
 *
 * MM/dd/yy HH:mm:ss
 *
 * A "relative" format that can be used to declare timestamps within the boundaries of the same day is:
 *
 * HH:mm:ss
 *
 * No quotation marks are necessary around the timestamp string, the parser knows how to handle the space between the
 * date section and the time section.
 *
 * The native type (as returned by getValue()) is java.util.Date.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class TimestampOption extends OptionBase {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String DEFAULT_FORMAT_AS_STRING = "MM/dd/yy HH:mm:ss";
    public static final DateFormat DEFAULT_FULL_FORMAT = new SimpleDateFormat(DEFAULT_FORMAT_AS_STRING);

    // HH:mm:ss
    public static final DateFormat DEFAULT_RELATIVE_FORMAT =
            new SimpleDateFormat(DEFAULT_FORMAT_AS_STRING.substring(DEFAULT_FORMAT_AS_STRING.indexOf(' ') + 1));

    // Static ----------------------------------------------------------------------------------------------------------

    /**
     * @return a valid Date that matches one of the known formats or null.
     */
    public static Date parseValue(String s) {

        try {
            return DEFAULT_FULL_FORMAT.parse(s);

        }
        catch(ParseException e) {
            // ignore, try next
        }

        try {
            return DEFAULT_RELATIVE_FORMAT.parse(s);

        }
        catch(ParseException e) {
            // ignore, try next
        }

        return null;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    private Date value;
    private boolean relative;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param shortLiteral the literal (without '-')
     */
    public TimestampOption(Character shortLiteral) {
        super(shortLiteral, null);
    }

    /**
     * @param longLiteral the literal (without '--')
     */
    public TimestampOption(String longLiteral) {
        super(null, longLiteral);
    }

    /**
     * @param shortLiteral the literal (without '-')
     * @param longLiteral the literal (without '--')
     */
    public TimestampOption(Character shortLiteral, String longLiteral) {
        super(shortLiteral, longLiteral);
    }

    /**
     * @param shortLiteral the literal (without '-')
     * @param longLiteral the literal (without '--')
     *
     * @exception ParseException if the String value cannot be parsed into a Date
     */
    public TimestampOption(Character shortLiteral, String longLiteral, String value) throws ParseException {

        super(shortLiteral, longLiteral);
        Date date = parseValue(value);
        if (date == null) {
            throw new ParseException("\"" + value + "\" does not match any of the known formats", 0);
        }
        setValue(date);
    }

    @Override
    public void setValue(Object o) {

        if (o != null && !(o instanceof Date)) {
            throw new IllegalArgumentException(o + " is not a Date");
        }

        this.value = (Date)o;
        this.relative = value != null && value.getTime() < 24L * 60 * 60 * 1000L;
    }

    // OptionBase overrides --------------------------------------------------------------------------------------------

    /**
     * @return a Date instance.
     */
    @Override
    public Date getValue() {
        return value;
    }

    @Override
    public String toString() {

        String s = value == null ?
                "" :
                (relative ? DEFAULT_RELATIVE_FORMAT.format(value) : DEFAULT_FULL_FORMAT.format(value));

        return toString(s);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return true if the timestamp is relative to the beginning of the day ("14:01:02") or false if the timestamp
     * is specified in full, including the day ("07/25/16 14:01:02").
     */
    public boolean isRelative() {

        return relative;
    }

    public String getString() {

        if (value == null) {
            return null;
        }

        if (relative) {

            return DEFAULT_RELATIVE_FORMAT.format(value);
        }
        else {

            return DEFAULT_FULL_FORMAT.format(value);
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

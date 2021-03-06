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

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public class StringOption extends OptionBase {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String value;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param shortLiteral the literal (without '-')
     */
    public StringOption(Character shortLiteral) {
        this(shortLiteral, null, null);
    }

    /**
     * @param longLiteral the literal (without '--')
     */
    public StringOption(String longLiteral) {
        this(null, longLiteral, null);
    }

    /**
     * @param shortLiteral the literal (without '-')
     * @param longLiteral the literal (without '--')
     */
    public StringOption(Character shortLiteral, String longLiteral) {
        this(shortLiteral, longLiteral, null);
    }

    /**
     * @param shortLiteral the literal (without '-')
     * @param longLiteral the literal (without '--')
     */
    public StringOption(Character shortLiteral, String longLiteral, String value) {
        super(shortLiteral, longLiteral);
        this.value = value;
    }

    @Override
    public void setValue(Object o) {

        if (o != null && !(o instanceof String)) {
            throw new IllegalArgumentException(o + " is not a String");
        }

        this.value = (String)o;
    }

    // OptionBase overrides --------------------------------------------------------------------------------------------

    @Override
    public String getValue() {
        return value;
    }


    // Public ----------------------------------------------------------------------------------------------------------

    public void setValue(String s) {
        this.value = s;
    }

    public String getString() {
        return getValue();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

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
public class VerboseOption extends BooleanOption {

    // Constants -------------------------------------------------------------------------------------------------------

    public static final String LONG_LITERAL = "verbose";
    public static final Character SHORT_LITERAL = 'v';

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    public VerboseOption() {
        super(SHORT_LITERAL, LONG_LITERAL);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {

        return o instanceof VerboseOption;
    }

    @Override
    public int hashCode() {

        return 1;
    }

    @Override
    public String toString() {

        return "-" + SHORT_LITERAL + "|--" + LONG_LITERAL;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

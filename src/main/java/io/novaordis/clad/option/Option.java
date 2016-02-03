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
 * A command-line option. Works as both global option or command option.
 *
 * It has a short form (-o <value>) and a long form (--option=<value>). Both form are equivalent.
 *
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public interface Option {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the short form literal character (-o <value>). May return null.
     */
    Character getShortLiteral();

    void setShortLiteral(Character c);

    /**
     * The long form literal character (--option=<value>). May return null.
     */
    String getLongLiteral();

    void setLongLiteral(String longLiteral);

    Object getValue();

    void setValue(Object o);

    /**
     * Must correctly implement equals() because we are relying on it on set operations. Two options are equal if
     * they have the same definition (for example -v is equal with --verbose). The value is not factored in.
     */
    boolean equals(Object o);

    /**
     * Must correctly implement hashCode() because we are relying on equals() on set operations.
     */
    int hashCode();

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

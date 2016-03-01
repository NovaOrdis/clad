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

import java.util.Set;

/**
 * A command-line option. Works as both global option or command option.
 *
 * It has a short form (-o <value>) and a long form (--option=<value>). Both forms can be used interchangeably.
 *
 * Equivalent Options.
 *
 * There are situation when the same semantics can be expressed with slightly different options. For example,
 * a format of some sort can be provided in command line with --format="..." or extracted from a file specified
 * with --format-file="...". We say the options are equivalent. If two options are equivalent, and the configuration
 * behind either of them is required, it is sufficient to declare just one of them required. If the other is provided
 * the required condition is met. The equivalence relation is a symmetric relation.
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

    /**
     * @return a human readable label, consisting in the long literal, short literal or both if both are present:
     *
     * "--long", "-s" or "-s|--long".
     */
    String getLabel();

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

    //
    // Equivalence -----------------------------------------------------------------------------------------------------
    //

    /**
     * @return a set of options equivalent to this one: if any of the options in the "equivalent" set is required,
     * any other from the set will do.
     *
     * @see Option#isEquivalentWith(Option)
     */
    Set<Option> getEquivalentOptions();

    /**
     * @return true if the given option is equivalent with this one - they have the same semantics: if any of the
     * options in the "equivalent" set is required, any other from the set will do. The equivalence relation is
     * symmetric.
     *
     * @see Option#getEquivalentOptions()
     */
    boolean isEquivalentWith(Option o);

    void addEquivalentOption(Option o);

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

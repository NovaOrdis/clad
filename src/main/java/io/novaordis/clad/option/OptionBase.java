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

import java.util.HashSet;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/26/16
 */
public abstract class OptionBase implements Option {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Character shortLiteral;
    private String longLiteral;
    private Set<Option> equivalentOptions;

    // Constructors ----------------------------------------------------------------------------------------------------

    /**
     * @param shortLiteral the literal (without '-')
     * @param longLiteral the literal (without '--')
     */
    protected OptionBase(Character shortLiteral, String longLiteral) {
        this.shortLiteral = shortLiteral;

        if (longLiteral != null && longLiteral.startsWith("--")) {
            throw new IllegalArgumentException("the long literal must not start with --, the dashes are implicit");
        }

        this.longLiteral = longLiteral;
        this.equivalentOptions = new HashSet<>();
    }

    // Option implementation -------------------------------------------------------------------------------------------

    @Override
    public Character getShortLiteral()
    {
        return shortLiteral;
    }

    @Override
    public void setShortLiteral(Character c)
    {
        this.shortLiteral = c;
    }

    @Override
    public String getLongLiteral()
    {
        return longLiteral;
    }

    @Override
    public void setLongLiteral(String s)
    {
        this.longLiteral = s;
    }

    @Override
    public String getLabel() {

        String s = null;

        if (shortLiteral != null) {
            s = "-" + shortLiteral;
        }

        if (s == null) {
            return "--" + longLiteral;
        }
        else {
            return s + (longLiteral == null ? "" : "|--" + longLiteral);
        }
    }
    //
    // Equivalence -----------------------------------------------------------------------------------------------------
    //

    /**
     * Returns the underlying storage, handle with care.
     */
    @Override
    public Set<Option> getEquivalentOptions() {

        return equivalentOptions;
    }

    @Override
    public void addEquivalentOption(Option o) {

        if (!equivalentOptions.contains(o)) {

            equivalentOptions.add(o);
            // insure symmetry
            o.addEquivalentOption(this);
        }
    }

    @Override
    public boolean isEquivalentWith(Option o) {

        return equivalentOptions.contains(o);
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public boolean equals(Object o) {

        if (this == o) {
            return true;
        }

        if (o == null) {
            return false;
        }

        //noinspection SimplifiableIfStatement
        if (!this.getClass().equals(o.getClass())) {
            return false;
        }

        return
                shortLiteral != null && shortLiteral.equals(((OptionBase)o).shortLiteral) ||
                        longLiteral != null && longLiteral.equals(((OptionBase)o).longLiteral);
    }

    @Override
    public int hashCode() {

        return
                (shortLiteral != null ? shortLiteral.hashCode() : 0) +
                        17 * (longLiteral != null ? longLiteral.hashCode() : 0);
    }

    @Override
    public String toString() {

        String value = getValue() == null ? "" : "\"" + getValue() + "\"";
        return toString(value);
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    protected String toString(String valueToString) {

        if (getShortLiteral() != null) {

            if (getLongLiteral() != null) {

                return "-" + getShortLiteral() + "|--" + getLongLiteral() + "=" + valueToString;
            }
            return "-" + getShortLiteral() + " " + valueToString;
        }
        else {
            return "--" + getLongLiteral() + "=" + valueToString;
        }
    }

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

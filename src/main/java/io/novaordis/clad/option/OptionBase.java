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
public abstract class OptionBase implements Option {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private Character shortLiteral;
    private String longLiteral;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected OptionBase(Character shortLiteral, String longLiteral) {
        this.shortLiteral = shortLiteral;
        this.longLiteral = longLiteral;
    }

    // Option implementation -------------------------------------------------------------------------------------------

    @Override
    public Character getShortLiteral()
    {
        return shortLiteral;
    }

    @Override
    public String getLongLiteral()
    {
        return longLiteral;
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

        if (getShortLiteral() != null) {

            if (getLongLiteral() != null) {

                return "-" + getShortLiteral() + "|--" + getLongLiteral() + "=" + value;
            }
            return "-" + getShortLiteral() + " " + value;
        }
        else {
            return "--" + getLongLiteral() + "=" + value;
        }
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

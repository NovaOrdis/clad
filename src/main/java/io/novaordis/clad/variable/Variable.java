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

package io.novaordis.clad.variable;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public class Variable implements Token {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String name;

    // Constructors ----------------------------------------------------------------------------------------------------

    public Variable(String variableName) {
        this.name = variableName;
    }

    // Token implementation --------------------------------------------------------------------------------------------

    @Override
    public String getLiteral() {

        if (name == null) {
            return null;
        }

        return "${" + name + "}";
    }

    @Override
    public String resolve(VariableProvider provider) {

        if (provider == null) {
            return getLiteral();
        }

        String value = provider.getValue(name);

        if (value == null) {
            return getLiteral();
        }

        return value;
    }

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * @return the variable name
     */
    public String getName() {

        return name;
    }

    @Override
    public String toString() {

        return getLiteral();
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

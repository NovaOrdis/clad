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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 11/22/16
 */
public class StringWithVariables {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private String literal;
    private List<Token> tokens;

    // Constructors ----------------------------------------------------------------------------------------------------

    public StringWithVariables(String stringWithVariables) throws VariableFormatException {

        if (stringWithVariables == null) {
            throw new IllegalArgumentException("null argument");
        }

        this.literal = stringWithVariables;
        this.tokens = new ArrayList<>();
        parse();
    }

    // Public ----------------------------------------------------------------------------------------------------------

    public String resolve(VariableProvider provider) {

        String s = "";

        for(Token t: tokens) {

            s += t.resolve(provider);
        }

        return s;
    }

    public String getLiteral() {
        return literal;
    }

    @Override
    public String toString() {
        return literal;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    List<Token> getTokens() {
        return tokens;
    }

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    private void parse() throws VariableFormatException {

        int crt = 0;
        int i;

        while(crt < literal.length()) {

            i = literal.indexOf("${", crt);

            if (i == -1 || i != crt) {

                //
                // constant
                //

                i = i != -1 ? i : literal.length();
                tokens.add(new Constant(literal.substring(crt, i)));
                crt = i;
            }
            else {

                //
                // variable
                //

                //
                // find the end bracket
                //

                crt = i;

                i = literal.indexOf('}', crt);

                if (i == -1) {

                    throw new VariableFormatException("invalid variable definition, missing closing bracket");
                }

                tokens.add(new Variable(literal.substring(crt + 2, i)));

                crt = i + 1;
            }

        }
    }

    // Inner classes ---------------------------------------------------------------------------------------------------

}

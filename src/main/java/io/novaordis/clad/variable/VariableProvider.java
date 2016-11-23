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
public interface VariableProvider {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    /**
     * The method to resolve variables. If the implementation has a value for the specific variable, it will return a
     * non-null string value. If the variable cannot be resolved, the method will return null.
     *
     * See ${linkUrl https://kb.novaordis.com/index.php/Clad_User_Manual_-_Concepts#Variable_Support} for more details.
     */
    String getValue(String variableName);

    /**
     * Use this method to install variables into the provider.
     *
     * @param variableValue the variable value. Can be null, in which case the semantics is to remove that variable.
     *
     * @return the old value of the variable, or null if none.
     */
    String setValue(String variableName, String variableValue);

}

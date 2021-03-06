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

package io.novaordis.clad.command;

import io.novaordis.clad.option.Option;
import io.novaordis.clad.option.OptionParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/29/16
 */
public abstract class CommandBase implements Command {

    // Constants -------------------------------------------------------------------------------------------------------

    // Static ----------------------------------------------------------------------------------------------------------

    // Attributes ------------------------------------------------------------------------------------------------------

    private List<Option> options;

    // Constructors ----------------------------------------------------------------------------------------------------

    protected CommandBase() {

        this.options = new ArrayList<>();
    }

    // Command implementation ------------------------------------------------------------------------------------------

    @Override
    public int compareTo(Command o) {

        if (o == null) {
            throw new NullPointerException();
        }

        return getName().compareTo(o.getName());
    }

    @Override
    public String getName() {

        String s = getClass().getSimpleName();
        s = s.replaceAll("Command", "");
        // detect camel case and inject dashes
        String name = "";
        for(int i = 0; i < s.length(); i ++) {

            char c = s.charAt(i);

            if (Character.isUpperCase(c)) {

                c = Character.toLowerCase(c);
                if (i > 0) {
                    name += '-';
                }
            }
            name += c;
        }
        return name;
    }

    @Override
    public String getHelpFilePath() {

        String s = getClass().getName();
        s = s.substring(0, s.lastIndexOf('.'));
        s = s.replace('.', '/');
        return s + "/" + getName() + ".txt";
    }

    @Override
    public boolean needsRuntime() {

        //
        // usually all application commands need the runtime
        //

        return true;
    }

    /**
     * By default, the base does not declare any required options.
     */
    @Override
    public Set<Option> requiredOptions() {
        return Collections.emptySet();
    }

    /**
     * By default, the base does not declare any optional options.
     */
    @Override
    public Set<Option> optionalOptions() {
        return Collections.emptySet();
    }

    /**
     * Returns the underlying storage.
     */
    @Override
    public List<Option> getOptions() {
        return options;
    }

    @Override
    public Option getOption(Option model) {

        if (model == null) {
            return null;
        }

        for(Option o: options) {
            if (model.equals(o)) {
                return o;
            }
        }

        return null;
    }

    @Override
    public void setOption(Option o) {

        options.add(o);
    }

    /**
     * The default implementation handles declared required and optional options and removes the associated strings
     * from the argument list. In most cases, this behavior is all subclasses need, and they should use it. The method
     * can be overridden when the command expects arguments, other than declared options. The override must process
     * the know arguments and remove them from the argument list, leaving the unknown arguments in the list.
     */
    @Override
    public void configure(int from, List<String> commandLineArguments) throws Exception {

        this.options = OptionParser.parse(from, commandLineArguments, requiredOptions(), optionalOptions());
    }

    // Public ----------------------------------------------------------------------------------------------------------

    @Override
    public String toString() {
        return getName() + " [" + Integer.toHexString(System.identityHashCode(this)) + "]";
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

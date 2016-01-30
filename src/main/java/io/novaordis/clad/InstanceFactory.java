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

package io.novaordis.clad;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.StringTokenizer;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;

/**
 * @author Ovidiu Feodorov <ovidiu@novaordis.com>
 * @since 1/29/16
 */
public class InstanceFactory {

    // Constants -------------------------------------------------------------------------------------------------------

    private static final Logger log = LoggerFactory.getLogger(InstanceFactory.class);

    public static final String JAVA_CLASS_PATH_SYSTEM_PROPERTY_NAME = "java.class.path";
    public static final String PATH_SEPARATOR_SYSTEM_PROPERTY_NAME = "path.separator";

    public static final int DIRECTORIES_ARE_SEARCHED_FIRST = 0;
    @SuppressWarnings("unused")
    public static final int JARS_ARE_SEARCHED_FIRST = 1;

    // Static ----------------------------------------------------------------------------------------------------------

    //
    // whether directories or the JARs from the class path are searched first.
    //
    public static int searchOrder = DIRECTORIES_ARE_SEARCHED_FIRST;

    /**
     * @return a non-initialized Command instance if the corresponding command implementation class was found on the
     * class path and the no-argument constructor instantiation went well.
     */
    public static Command getCommand(String name) throws Exception {

        String commandClassName = getFullyQualifiedClassName(name, "Command");

        if (commandClassName == null) {
            return null;
        }

        //
        // we identified a class file in the class path whose name matches a command class file pattern, so try to load
        // it
        //

        Class commandClass;

        try {
            commandClass = InstanceFactory.class.getClassLoader().loadClass(commandClassName);
        }
        catch(Exception e) {
            throw new IllegalStateException("failed to load Command class " + commandClassName);
        }

        try {
            return (Command)commandClass.newInstance();
        }
        catch(Exception e) {
            throw new IllegalStateException("failed to instantiate Command class " + commandClass);
        }
    }

    /**
     * @return the fully qualified class name of the class that corresponds to the given prefix (which will be
     * camel-cased) and the given suffix (currently Command or ApplicationRuntime). It will return null if no such
     * class is detected on the classpath.
     */
    public static String getFullyQualifiedClassName(String prefix, String suffix) throws Exception {

        //
        // scan the classpath and look for classes named camelCased(prefix) + suffix.
        //

        String classPath = System.getProperty(JAVA_CLASS_PATH_SYSTEM_PROPERTY_NAME);

        if (classPath == null) {
            throw new IllegalArgumentException(
                    "no value found for system property '" + JAVA_CLASS_PATH_SYSTEM_PROPERTY_NAME + "'");
        }

        String pathSeparator = System.getProperty(PATH_SEPARATOR_SYSTEM_PROPERTY_NAME);

        if (pathSeparator == null) {
            throw new IllegalArgumentException(
                    "no value found for system property '" + PATH_SEPARATOR_SYSTEM_PROPERTY_NAME + "'");
        }

        List<File> directories = new ArrayList<>();
        List<JarFile> jarFiles = new ArrayList<>();

        for(StringTokenizer st = new StringTokenizer(classPath, pathSeparator); st.hasMoreTokens(); ) {

            String path = st.nextToken();
            log.debug("path: " + path);
            File f = new File(path);
            if (f.isDirectory()) {

                //
                // directory containing classes, possibly commands
                //

                log.debug("directory: " + path);
                directories.add(f);

            }
            else if (f.isFile()) {

                //
                // we assume it's a JAR
                //

                JarFile jarFile = new JarFile(f.getPath());
                log.debug("JAR file: " + jarFile);
                jarFiles.add(jarFile);
            }
        }

        String simpleClassName = toSimpleClassName(prefix, suffix);
        String fullyQualifiedClassNameRegex = ".*\\." + simpleClassName;

        List<String> fqcns;

        //
        // by default directories have priority in search
        //

        int so = searchOrder;

        for(int counter = 0; counter < 2; counter ++) {

            if (so == DIRECTORIES_ARE_SEARCHED_FIRST) {

                //
                // Directories are searched now
                //

                fqcns = getFullyQualifiedClassNamesFromDirectories(fullyQualifiedClassNameRegex, directories);

            }
            else {

                //
                // JARs are searched now
                //

                fqcns = getFullyQualifiedClassNamesFromJars(fullyQualifiedClassNameRegex, jarFiles);
            }

            if (fqcns.size() > 1) {
                throw new UserErrorException("more than one class matches " + simpleClassName + ": " + fqcns);
            }

            if (fqcns.size() == 1) {

                //
                // just one, found it
                //
                return fqcns.get(0);
            }

            //
            // we did not find it in directories/JAR, try to find it in JARs/directories by looping or exit
            //

            so = (so + 1) % 2;
        }

        return null;
    }

    public static String toSimpleClassName(String prefix, String suffix) {

        if (prefix == null) {
            throw new IllegalArgumentException("null prefix");
        }

        if (suffix == null) {
            throw new IllegalArgumentException("null suffix");
        }


        return Character.toUpperCase(prefix.charAt(0)) + prefix.substring(1).toLowerCase() + suffix;
    }

    /**
     * Recursively scan the given directories looking for files whose paths, relative to those directories, match
     * the given pattern. Useful when looking for specific classes in a classpath directory.
     *
     * @param fullyQualifiedClassNameRegex the Java regular expression to match against the fully qualified name.
     *                                     Must NOT include .class at the end, that is appended internally.
     */
    public static List<String> getFullyQualifiedClassNamesFromDirectories(
            String fullyQualifiedClassNameRegex, List<File> dirs) {

        if (fullyQualifiedClassNameRegex.endsWith(".class")) {
            throw new IllegalArgumentException("the fully qualified class name regular expression must not end in '.class'");
        }

        Pattern pattern = Pattern.compile(fullyQualifiedClassNameRegex + "\\.class");

        List<String> result = new ArrayList<>();

        for(File dir: dirs) {

            if (!dir.isDirectory()) {
                continue;
            }

            String dirName = dir.getPath();

            //noinspection Convert2streamapi
            for(String fileName: getFileNames(dir.getPath(), dir)) {

                String candidateClassName = fileName.substring(dirName.length() + 1);
                candidateClassName = candidateClassName.replace(File.separator, ".");

                if (pattern.matcher(candidateClassName).matches()) {
                    candidateClassName =
                            candidateClassName.substring(0, candidateClassName.length() - ".class".length());
                    result.add(candidateClassName);
                }
            }
        }

        return result;
    }

    /**
     * Recursively scan the given JAR files looking for embedded files whose paths, relative to those JARs, match
     * the given pattern. Useful when looking for specific classes in classpath JAR.
     *
     * @param fullyQualifiedClassNameRegex the Java regular expression to match against the fully qualified name.
     *                                     Must NOT include .class at the end, that is appended internally.
     */
    public static List<String> getFullyQualifiedClassNamesFromJars(
            String fullyQualifiedClassNameRegex, List<JarFile> jarFiles) {

        if (fullyQualifiedClassNameRegex.endsWith(".class")) {
            throw new IllegalArgumentException("the fully qualified class name regular expression must not end in '.class'");
        }

        Pattern pattern = Pattern.compile(fullyQualifiedClassNameRegex + "\\.class");

        List<String> result = new ArrayList<>();

        for(JarFile jarFile: jarFiles) {

            for(Enumeration<JarEntry> entries = jarFile.entries(); entries.hasMoreElements(); ) {
                JarEntry entry = entries.nextElement();
                String className = entry.getName().replace(File.separatorChar, '.');
                if (pattern.matcher(className).matches()) {
                    className = className.substring(0, className.length() - ".class".length());
                    result.add(className);
                }
            }
        }

        return result;
    }

    // Attributes ------------------------------------------------------------------------------------------------------

    // Constructors ----------------------------------------------------------------------------------------------------

    // Public ----------------------------------------------------------------------------------------------------------

    // Static Package protected -----------------------------------------------------------------------------------------------

    /**
     * @return all files contained by the given directory. The file name is relative to the given directory.
     */
    static List<String> getFileNames(String relativePath, File dir) {

        List<String> result = new ArrayList<>();

        if (!dir.isDirectory()) {
            throw new IllegalArgumentException(dir + " is not a directory");
        }

        File[] content = dir.listFiles();

        if (content != null) {

            for (File f : content) {

                if (f.isFile()) {
                    result.add(relativePath + File.separator + f.getName());
                } else if (f.isDirectory()) {
                    result.addAll(getFileNames(relativePath + File.separator + f.getName(), f));
                }
            }
        }

        return result;
    }

    // Package protected -----------------------------------------------------------------------------------------------

    // Protected -------------------------------------------------------------------------------------------------------

    // Static Private --------------------------------------------------------------------------------------------------

    // Private ---------------------------------------------------------------------------------------------------------

    // Inner classes ---------------------------------------------------------------------------------------------------

}

/*
 * Copyright 2020  vivier technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.vivier_technologies.utils;

import org.apache.commons.cli.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public class ConfigReader {
    private static final byte[] _loggingKey = Logger.generateLoggingKey("CONFIG_READ");

    /**
     * Process the command line passed in with set of custom options in addition to the standard ones
     *
     * @param options custom options object
     * @param logger logger instance
     * @param args args passed to main
     * @return CommandLine object
     * @throws ParseException if unable to parse
     */
    public static CommandLine getCommandLine(Options options, Logger logger, String[] args) throws ParseException{
        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder
                .withArgName("configtype")
                .hasArg()
                .withDescription("Whether to initialise using file based or url based config")
                .isRequired()
                .create("configtype"));
        //noinspection AccessStaticViaInstance
        options.addOption(OptionBuilder
                .withArgName("config")
                .hasArg()
                .withDescription("Either a url to the properties file or a config file")
                .isRequired()
                .create("config"));

        CommandLineParser parser = new BasicParser();
        CommandLine commandLine = null;
        commandLine = parser.parse(options, args);

        logger.info(ConfigReader._loggingKey, "Command line options:");
        for (Option option : commandLine.getOptions()) {
            logger.info(ConfigReader._loggingKey, option.getArgName(), "=", option.getValue());
        }
        return commandLine;
    }

    /**
     * Get configuration assuming you already have a parsed command line
     *
     * @param commandLine command line instance
     * @param logger logger instance
     * @param args args passed in
     * @return Configuration object
     * @throws ConfigurationException if configuration unable to be parsed
     */
    public static Configuration getConfig(CommandLine commandLine, Logger logger, String[] args) throws ConfigurationException {
        Configuration config;
        // TODO add code to download from remote URL
        if (commandLine.getOptionValue("configtype").equalsIgnoreCase("file")) {
            config = new Configurations().properties(new File(commandLine.getOptionValue("config")));
        } else {
            throw new IllegalArgumentException("Only configtype option of file is supported at present");
        }
        return config;
    }

    /**
     * Get configuration using custom options
     *
     * @param options custom options
     * @param logger logger instance
     * @param args args passed in
     * @return Configuration object
     * @throws ParseException if unable to parse command line
     * @throws ConfigurationException if unable to parse configuration file
     */
    public static Configuration getConfig(Options options, Logger logger, String[] args)
            throws ParseException, ConfigurationException {

        CommandLine commandLine = getCommandLine(options, logger, args);
        return getConfig(commandLine, logger, args);
    }

    /**
     * Get configuration using standard options
     *
     * @param logger logger instance
     * @param args args passed in
     * @return Configuration object
     * @throws ParseException if unable to parse command line
     * @throws ConfigurationException if configuration has errors
     */
    public static Configuration getConfig(Logger logger, String[] args) throws ParseException, ConfigurationException {
        Options options = new Options();
        return getConfig(options, logger, args);
    }
}

package com.vivier_technologies.utils;

import org.apache.commons.cli.*;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;

public class ConfigReader {
    private static final byte[] _loggingKey = Logger.generateLoggingKey("CONFIG_READ");

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

    public static Configuration getConfig(CommandLine commandLine, Logger logger, String[] args)
            throws ParseException, ConfigurationException {
        Configuration config;
        // TODO add code to download from remote URL
        if (commandLine.getOptionValue("configtype").equalsIgnoreCase("file")) {
            config = new Configurations().properties(new File(commandLine.getOptionValue("config")));
        } else {
            throw new IllegalArgumentException("Only configtype option of file is supported at present");
        }
        return config;
    }

    public static Configuration getConfig(Options options, Logger logger, String[] args)
            throws ParseException, ConfigurationException {

        CommandLine commandLine = getCommandLine(options, logger, args);
        return getConfig(commandLine, logger, args);
    }

    public static Configuration getConfig(Logger logger, String[] args) throws ParseException, ConfigurationException {
        Options options = new Options();
        return getConfig(options, logger, args);
    }
}

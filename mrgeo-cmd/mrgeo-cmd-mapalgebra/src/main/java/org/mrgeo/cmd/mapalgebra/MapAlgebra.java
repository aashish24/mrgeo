/*
 * Copyright 2009-2015 DigitalGlobe, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations under the License.
 */

package org.mrgeo.cmd.mapalgebra;

import org.apache.commons.cli.*;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.mapreduce.Job;
import org.mrgeo.aggregators.MeanAggregator;
import org.mrgeo.buildpyramid.BuildPyramidSpark;
import org.mrgeo.cmd.Command;
import org.mrgeo.core.MrGeoConstants;
import org.mrgeo.core.MrGeoProperties;
import org.mrgeo.data.DataProviderFactory;
import org.mrgeo.data.DataProviderFactory.AccessMode;
import org.mrgeo.data.ProtectionLevelUtils;
import org.mrgeo.data.image.MrsImageDataProvider;
import org.mrgeo.mapalgebra.MapAlgebraExecutioner;
import org.mrgeo.mapalgebra.MapAlgebraParser;
import org.mrgeo.mapalgebra.MapOp;
import org.mrgeo.mapalgebra.RasterMapOp;
import org.mrgeo.mapreduce.job.JobCancelledException;
import org.mrgeo.mapreduce.job.JobFailedException;
import org.mrgeo.progress.ProgressHierarchy;
import org.mrgeo.utils.HadoopUtils;
import org.mrgeo.utils.LoggingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class MapAlgebra extends Command
{
  private static Logger log = LoggerFactory.getLogger(MapAlgebra.class);

  public static Options createOptions()
  {
    Options result = new Options();

    Option expression = new Option("e", "expression", true, "Expression to calculate");
    expression.setRequired(false);
    result.addOption(expression);

    Option output = new Option("o", "output", true, "Output path");
    output.setRequired(true);
    result.addOption(output);

    Option script = new Option("s", "script", true, "Path to the script to execute");
    script.setRequired(false);
    result.addOption(script);

    Option buildPyramids = 
        new Option("b", "buildPyramids", false, "Build pyramids on the job output.");
    buildPyramids.setRequired(false);
    result.addOption(buildPyramids);

    Option local = new Option("l", "local-runner", false, "Use Hadoop's local runner (used for debugging)");
    local.setRequired(false);
    result.addOption(local);

    Option protectionLevelOption = new Option("pl", "protectionLevel", true, "Protection level");
    // If mrgeo.conf security.classification.required is true and there is no
    // security.classification.default, then the security classification
    // argument is required, otherwise it is not.
    Properties props = MrGeoProperties.getInstance();
    String protectionLevelRequired = props.getProperty(
        MrGeoConstants.MRGEO_PROTECTION_LEVEL_REQUIRED, "false").trim();
    String protectionLevelDefault = props.getProperty(
        MrGeoConstants.MRGEO_PROTECTION_LEVEL_DEFAULT, "");
    if (protectionLevelRequired.equalsIgnoreCase("true") &&
        protectionLevelDefault.isEmpty())
    {
      protectionLevelOption.setRequired(true);
    }
    else
    {
      protectionLevelOption.setRequired(false);
    }
    result.addOption(protectionLevelOption);

    result.addOption(new Option("v", "verbose", false, "Verbose logging"));
    result.addOption(new Option("d", "debug", false, "Debug (very verbose) logging"));

    return result;
  }

  @Override
  public int run(String[] args, Configuration conf, final Properties providerProperties)
  {

    System.out.println(log.getClass().getName());

    try
    {
      Options options = MapAlgebra.createOptions();
      CommandLine line = null;
      try
      {
        CommandLineParser parser = new PosixParser();
        line = parser.parse(options, args);
      }
      catch (ParseException e)
      {
        System.out.println();
        new HelpFormatter().printHelp("MapAlgebra", options);
        return 1;
      }

      if (line == null)
      {
        new HelpFormatter().printHelp("MapAlgebra", options);
        return 1;
      }

      String expression = line.getOptionValue("e");
      String output = line.getOptionValue("o");
      String script = line.getOptionValue("s");

      if (expression == null && script == null)
      {
        System.out.println("Either an expression or script must be specified.");
        System.out.println();
        new HelpFormatter().printHelp("MapAlgebra", options);
        return 1;
      }

      if (script != null)
      {
        File f = new File(script);
        byte[] buffer = new byte[(int) f.length()];
        FileInputStream fis = new FileInputStream(f);
        fis.read(buffer);
        expression = new String(buffer);
        fis.close();
      }

      try
      {
        if (line.hasOption("v"))
        {
          LoggingUtils.setDefaultLogLevel(LoggingUtils.INFO);
        }
        if (line.hasOption("d"))
        {
          LoggingUtils.setDefaultLogLevel(LoggingUtils.DEBUG);
        }

        if (line.hasOption("l"))
        {
          System.out.println("Using local runner");
          HadoopUtils.setupLocalRunner(conf);
        }

        String protectionLevel = line.getOptionValue("pl");

        log.debug("expression: " + expression);
        log.debug("output: " + output);

        Job job = new Job();
        job.setJobName("MapAlgebra");

        MrsImageDataProvider dp = DataProviderFactory.getMrsImageDataProvider(output, AccessMode.OVERWRITE, providerProperties);
        String useProtectionLevel = ProtectionLevelUtils.getAndValidateProtectionLevel(dp, protectionLevel);
        MapAlgebraParser parser = new MapAlgebraParser(conf, useProtectionLevel, providerProperties);
        MapOp root = parser.parse(expression);

        log.debug("inputs: " + root.getInputs().toString());

        MapAlgebraExecutioner executioner = new MapAlgebraExecutioner();

        executioner.setOutputName(output);
        executioner.setRoot(root);
        ProgressHierarchy progress = new ProgressHierarchy();
        executioner.execute(conf, progress);

        if (progress.isFailed())
        {
          throw new JobFailedException(progress.getResult());
        }

        if (line.hasOption("b") && (root instanceof RasterMapOp))
        {
          System.out.println("Building pyramids...");
          BuildPyramidSpark.build(output.toString(), new MeanAggregator(), conf, providerProperties);
        }

        System.out.println("Output written to: " + output);
      }
      catch (IOException e)
      {
        e.printStackTrace();
        return 1;
      }
      catch (JobFailedException e)
      {
        e.printStackTrace();
        return 1;
      }
      catch (JobCancelledException e)
      {
        e.printStackTrace();
        return 1;
      }

      return 0;
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }

    return -1;
  }
}

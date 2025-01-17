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

package org.mrgeo.test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.mrgeo.hdfs.utils.HadoopFileUtils;
import org.mrgeo.image.MrsImage;
import org.mrgeo.image.MrsImagePyramid;
import org.mrgeo.image.RasterTileMerger;
import org.mrgeo.mapalgebra.MapAlgebraExecutioner;
import org.mrgeo.mapalgebra.MapAlgebraParser;
import org.mrgeo.mapalgebra.MapOp;
import org.mrgeo.mapalgebra.parser.ParserException;
import org.mrgeo.mapreduce.job.JobCancelledException;
import org.mrgeo.mapreduce.job.JobFailedException;
import org.mrgeo.progress.ProgressHierarchy;
import org.mrgeo.rasterops.GeoTiffExporter;
import org.mrgeo.rasterops.OpImageRegistrar;
import org.mrgeo.utils.Bounds;
import org.mrgeo.utils.TMSUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MapOpTestUtils extends TestUtils
{
  private static final Logger log = LoggerFactory.getLogger(MapOpTestUtils.class);

  public MapOpTestUtils(final Class<?> testClass) throws IOException
  {
    super(testClass);

    OpImageRegistrar.registerMrGeoOps();
  }


  public void generateBaselinePyramid(final Configuration conf, final String testName,
      final String ex) throws IOException, JobFailedException, JobCancelledException,
      ParserException
      {

    runMapAlgebraExpression(conf, testName, ex);

    final Path src = new Path(outputHdfs, testName);
    final MrsImagePyramid pyramid = MrsImagePyramid.open(src.toString(), (Properties)null);
    if (pyramid != null)
    {
      final Path dst = new Path(inputLocal, testName);
      final FileSystem fs = dst.getFileSystem(conf);
      fs.copyToLocalFile(src, dst);
    }
      }

  public void generateBaselineTif(final Configuration conf, final String testName, final String ex)
      throws IOException, JobFailedException, JobCancelledException, ParserException
      {
    generateBaselineTif(conf, testName, ex, Double.NaN);
      }
  
  public void generateBaselineTif(final Configuration conf, final String testName,
      final String ex, double nodata)
      throws IOException, JobFailedException, JobCancelledException, ParserException
      {
    runMapAlgebraExpression(conf, testName, ex);

    final MrsImagePyramid pyramid = MrsImagePyramid.open(new Path(outputHdfs, testName).toString(),
        (Properties)null);
    final MrsImage image = pyramid.getHighestResImage();

    Bounds bounds = pyramid.getBounds();
    TMSUtils.Bounds tb = new TMSUtils.Bounds(bounds.getMinX(), bounds.getMinY(), bounds.getMaxX(),
        bounds.getMaxY());
    tb = TMSUtils.tileBounds(tb, pyramid.getMaximumLevel(), pyramid.getMetadata().getTilesize());
    bounds = new Bounds(tb.w, tb.s, tb.e, tb.n);
    try
    {
      final File baselineTif = new File(new File(inputLocal), testName + ".tif");
      GeoTiffExporter
      .export(image.getRenderedImage(bounds), bounds, baselineTif, false, nodata);
    }
    finally
    {
      if (image != null)
      {
        image.close();
      }
    }
      }

  public MapOp
  runRasterExpression(final Configuration conf, final String testName, final String ex)
      throws ParserException, IOException, JobFailedException, JobCancelledException
  {
    return runRasterExpression(conf, testName, null, ex);
  }

  public MapOp
  runRasterExpression(final Configuration conf, final String testName,
      final TestUtils.ValueTranslator baselineTranslator, final String ex)
      throws ParserException, IOException, JobFailedException, JobCancelledException
  {
    final MapOp mapOp = runMapAlgebraExpression(conf, testName, ex);

    compareRasterOutput(testName, baselineTranslator, (Properties)null);
    return mapOp;
  }

  public MapOp runRasterExpressionMultipleOutputs(final Configuration conf,
      final String testName,
      final String[] testOutputs, final String ex, final Properties providerProperties)
          throws ParserException, IOException, JobFailedException, JobCancelledException
          {
    final MapOp mapOp = runMapAlgebraExpression(conf, testName, ex);
    for (final String testOutput : testOutputs)
    {
      compareRasterOutput(testName + "/" + testOutput, providerProperties);
    }
    return mapOp;
          }


  private void compareRasterOutput(final String testName, final Properties providerProperties) throws IOException
  {
    compareRasterOutput(testName, null, providerProperties);
  }

  private void compareRasterOutput(final String testName, final TestUtils.ValueTranslator baselineTranslator,
      final Properties providerProperties) throws IOException
  {
    final MrsImagePyramid pyramid = MrsImagePyramid.open(new Path(outputHdfs, testName).toString(),
        providerProperties);
    final MrsImage image = pyramid.getHighestResImage();

    try
    {
      // The output against which to compare could either be a tif or a MrsImagePyramid.
      // We check for the tif first.
      final File file = new File(inputLocal);
      final File baselineTif = new File(file, testName + ".tif");
      if (baselineTif.exists())
      {
        TestUtils.compareRasters(baselineTif, baselineTranslator, RasterTileMerger.mergeTiles(image), null);
      }
      else
      {
        final String inputLocalAbs = file.getCanonicalFile().toURI().toString();
        final MrsImagePyramid goldenPyramid = MrsImagePyramid.open(inputLocalAbs + "/" + testName,
            providerProperties);
        final MrsImage goldenImage = goldenPyramid.getImage(image.getZoomlevel());
        try
        {
          TestUtils
          .compareRasters(RasterTileMerger.mergeTiles(goldenImage), RasterTileMerger.mergeTiles(image));
        }
        finally
        {
          if (goldenImage != null)
          {
            goldenImage.close();
          }
        }
      }
    }
    finally
    {
      if (image != null)
      {
        image.close();
      }
    }
  }

  /**
   * Runs the map algebra expression and stores the results to outputHdfs in  a
   * subdirectory that matches the testName. No comparison against expected
   * output is done. See other methods in this class like runVectorExpression and
   * runRasterExpression for that capability.
   * 
   * @param conf
   * @param testName
   * @param ex
   * @return
   * @throws IOException
   * @throws JobFailedException
   * @throws JobCancelledException
   * @throws ParserException
   */
  public MapOp runMapAlgebraExpression(final Configuration conf, final String testName, final String ex)
          throws IOException, JobFailedException, JobCancelledException, ParserException
  {
    HadoopFileUtils.delete(new Path(outputHdfs, testName));

    log.info(ex);
    final MapAlgebraParser parser = new MapAlgebraParser(conf, "", null);
//    parser.addPath(inputHdfs.toString());
    final MapAlgebraExecutioner mae = new MapAlgebraExecutioner();
    
    final MapOp mapOp = parser.parse(ex);
    log.info("\nMap Algebra Expression:\n" + MapAlgebraParser.toString(mapOp));

    mae.setRoot(mapOp);
    mae.setOutputName(new Path(outputHdfs, testName).toString());
    mae.execute(conf, new ProgressHierarchy());

    return mapOp;
  }
}

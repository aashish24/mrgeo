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

package org.mrgeo.services.mrspyramid.rendering;

import org.mrgeo.image.MrsImagePyramid;
import org.mrgeo.utils.Bounds;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import java.awt.image.Raster;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Interface to implement for classes writing images requested by the WMS to an HTTP response;
 * Implementing class names must begin with the image format name they handle (e.g. "Png") and end
 * with "ImageResponseWriter". This class must stay in the same namespace as ImageHandlerFactory.
 */
public interface ImageResponseWriter
{
  String[] getMimeTypes();

  /**
   * Returns the mime type for the color scale applier
   * 
   * @return a mime type string
   */
  String getResponseMimeType();

  String[] getWmsFormats();

  Response.ResponseBuilder write(final Raster raster);
  void write(final Raster raster, final HttpServletResponse response) throws ServletException;
  
  Response.ResponseBuilder write(final Raster raster, double[] defaults);
  void write(final Raster raster, double[] defaults, final HttpServletResponse response) throws ServletException;

  Response.ResponseBuilder write(final Raster raster, final int tileColumn, final int tileRow, final double scale,
    final MrsImagePyramid pyramid) throws IOException;
  void write(final Raster raster, final int tileColumn, final int tileRow, final double scale,
    final MrsImagePyramid pyramid, final HttpServletResponse response) throws ServletException, IOException;

  /**
   * 
   * @param imageName
   * @param imageName
   * @return
   * @throws IOException
   */
  Response.ResponseBuilder write(final Raster raster, final String imageName, final Bounds bounds);

  void write(final Raster raster, final String imageName, final Bounds bounds,
    final HttpServletResponse response) throws ServletException;
  
  void writeToStream(final Raster raster, double[] defaults, final ByteArrayOutputStream byteStream)
      throws IOException;
}

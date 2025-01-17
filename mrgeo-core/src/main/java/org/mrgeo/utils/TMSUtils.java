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

package org.mrgeo.utils;

/**
 * 
 */

import java.io.Serializable;

/**
 * 
 */
public class TMSUtils
{
  // TODO - Replace org.mrgeo.utils.Bounds with this one. This one is heavily used in v2
  // whereas the other one is legacy. At its core, its just four double fields, so no real reason
  // to choose the old one over new.
  public static class Bounds implements Serializable
  {
    public double n;
    public double s;
    public double e;
    public double w;

    public static final Bounds WORLD = new Bounds(-180, -90, 180, 90);

    public Bounds(final double w, final double s, final double e, final double n)
    {
      this.n = n;
      this.s = s;
      this.e = e;
      this.w = w;
    }

    public static org.mrgeo.utils.Bounds asBounds(final Bounds newBounds)
    {
      return new org.mrgeo.utils.Bounds(newBounds.w, newBounds.s, newBounds.e, newBounds.n);
    }

    public static Bounds asTMSBounds(final org.mrgeo.utils.Bounds oldBounds)
    {
      return new Bounds(oldBounds.getMinX(), oldBounds.getMinY(), oldBounds.getMaxX(), oldBounds
        .getMaxY());
    }

    public static org.mrgeo.utils.Bounds convertNewToOldBounds(final Bounds newBounds)
    {
      return new org.mrgeo.utils.Bounds(newBounds.w, newBounds.s, newBounds.e, newBounds.n);
    }

    public static Bounds convertOldToNewBounds(final org.mrgeo.utils.Bounds oldBounds)
    {
      return new Bounds(oldBounds.getMinX(), oldBounds.getMinY(), oldBounds.getMaxX(), oldBounds
        .getMaxY());
    }

    public org.mrgeo.utils.Bounds asBounds()
    {
      return new org.mrgeo.utils.Bounds(w, s, e, n);
    }

    public boolean contains(final Bounds b)
    {
      return contains(b, true);
    }

    /**
     * Is the bounds fully contained within this bounds. Edges are included iff includeAdjacent is
     * true
     */
    public boolean contains(final Bounds b, final boolean includeAdjacent)
    {
      if (includeAdjacent)
      {
        return (b.w >= w && b.s >= s && b.e <= e && b.n <= n);
      }
      return (b.w > w && b.s > s && b.e < e && b.n < n);
    }

    public org.mrgeo.utils.Bounds convertNewToOldBounds()
    {
      return new org.mrgeo.utils.Bounds(w, s, e, n);
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (this == obj)
      {
        return true;
      }
      if (obj == null)
      {
        return false;
      }
      if (getClass() != obj.getClass())
      {
        return false;
      }
      final Bounds other = (Bounds) obj;
      if (Double.doubleToLongBits(e) != Double.doubleToLongBits(other.e))
      {
        return false;
      }
      if (Double.doubleToLongBits(n) != Double.doubleToLongBits(other.n))
      {
        return false;
      }
      if (Double.doubleToLongBits(s) != Double.doubleToLongBits(other.s))
      {
        return false;
      }
      if (Double.doubleToLongBits(w) != Double.doubleToLongBits(other.w))
      {
        return false;
      }
      return true;
    }

    public void expand(final Bounds b)
    {
      if (n < b.n)
      {
        n = b.n;
      }
      if (s > b.s)
      {
        s = b.s;
      }

      if (w > b.w)
      {
        w = b.w;
      }

      if (e < b.e)
      {
        e = b.e;
      }
    }

    public void expand(final double lat, final double lon)
    {
      if (n < lat)
      {
        n = lat;
      }
      if (s > lat)
      {
        s = lat;
      }

      if (w > lon)
      {
        w = lon;
      }

      if (e < lon)
      {
        e = lon;
      }

    }

    public void
      expand(final double west, final double south, final double east, final double north)
    {
      if (n < north)
      {
        n = north;
      }
      if (s > south)
      {
        s = south;
      }

      if (w > west)
      {
        w = west;
      }

      if (e < east)
      {
        e = east;
      }

    }

    @Override
    public int hashCode()
    {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(e);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(n);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(s);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      temp = Double.doubleToLongBits(w);
      result = prime * result + (int) (temp ^ (temp >>> 32));
      return result;
    }

    public boolean intersect(final Bounds b)
    {
      return intersect(b, true);
    }

    /**
     * If the two boundaries are adjacent, this would return true iff includeAdjacent is true
     * 
     * @param b
     * @param includeAdjacent
     * @return
     */
    public boolean intersect(final Bounds b, final boolean includeAdjacent)
    {
      final Bounds intersectBounds = new Bounds(Math.max(this.w, b.w), Math.max(this.s, b.s), Math
        .min(this.e, b.e), Math.min(this.n, b.n));
      if (includeAdjacent)
      {
        return (intersectBounds.w <= intersectBounds.e && intersectBounds.s <= intersectBounds.n);
      }
      return (intersectBounds.w < intersectBounds.e && intersectBounds.s < intersectBounds.n);
    }

    @Override
    public String toString()
    {
      return "Bounds [w=" + w + ", s=" + s + ", e=" + e + ", n=" + n + "]";
    }

    public String toCommaString()
    {
      return w + "," + s + "," + e + "," + n;
    }

    public Bounds union(final Bounds b)
    {
      return new Bounds(Math.min(this.w, b.w), Math.min(this.s, b.s), Math.max(
          this.e, b.e), Math.max(this.n, b.n));
    }

    public Bounds intersection(final Bounds b)
    {
      return intersection(b, true);
    }

    /**
     * If the two boundaries are adjacent, this would return true iff includeAdjacent is true
     *
     * @param b
     * @param includeAdjacent
     * @return
     */
    public Bounds intersection(final Bounds b, final boolean includeAdjacent)
    {

      final Bounds intersectBounds = new Bounds(Math.max(this.w, b.w), Math.max(this.s, b.s), Math
          .min(this.e, b.e), Math.min(this.n, b.n));
      if (includeAdjacent)
      {
        if (intersectBounds.w <= intersectBounds.e && intersectBounds.s <= intersectBounds.n)
        {
          return intersectBounds;
        }
      }
      else if (intersectBounds.w < intersectBounds.e && intersectBounds.s < intersectBounds.n)
      {
        return intersectBounds;
      }

      return null;
    }

    public double width()
    {
      return e - w;
    }

    public double height()
    {
      return n - s;
    }

  }


  public static class LatLon implements Serializable
  {
    final public double lat;
    final public double lon;

    public LatLon(final double lat, final double lon)
    {
      this.lat = lat;
      this.lon = lon;
    }

    @Override
    public String toString()
    {
      return String.format("LatLon [lon (x): %f  lat(y): %f]", lon, lat);
    }
  }

  public static class Pixel implements Serializable
  {
    final public long px;
    final public long py;

    public Pixel(final long px, final long py)
    {
      this.px = px;
      this.py = py;
    }

    @Override
    public boolean equals(final Object obj)
    {
      if (obj instanceof TMSUtils.Pixel)
      {
        final TMSUtils.Pixel other = (TMSUtils.Pixel) obj;
        return (px == other.px) && (py == other.py);
      }
      return super.equals(obj);
    }

    @Override
    public int hashCode()
    {
      // Based on Point2D.hashCode() implementation
      long bits = px;
      bits ^= py * 31;
      return (((int) bits) ^ ((int) (bits >> 32)));
    }

    @Override
    public String toString()
    {
      return "Pixel [px=" + px + ", py=" + py + "]";
    }
  }

  public static class Tile implements Comparable<Tile>, Serializable
  {
    final public long tx;
    final public long ty;

    public Tile(final long tx, final long ty)
    {
      this.tx = tx;
      this.ty = ty;
    }

    @Override
    public int compareTo(final Tile tile)
    {
      if (this.ty == tile.ty && this.tx == tile.tx)
      {
        return 0;
      }
      else if (this.ty < tile.ty || (this.ty == tile.ty && this.tx < tile.tx))
      {
        return -1;
      }
      return 1;
    }

    @Override
    public String toString()
    {
      return "Tile [tx=" + tx + ", ty=" + ty + "]";
    }
  }

  public static class TileBounds implements Serializable
  {
    public long n;
    public long s;
    public long e;
    public long w;

    public TileBounds(final long tx, final long ty)
    {
      this.n = ty;
      this.s = ty;
      this.e = tx;
      this.w = tx;
    }

    public TileBounds(final long w, final long s, final long e, final long n)
    {
      this.n = n;
      this.s = s;
      this.e = e;
      this.w = w;
    }

    public TileBounds(final Tile t)
    {
      this.n = t.ty;
      this.s = t.ty;
      this.e = t.tx;
      this.w = t.tx;
    }

    public long width()
    {
      return e - w + 1;
    }

    public long height()
    {
      return n - s + 1;
    }

    public LongRectangle toLongRectangle()
    {
      return new LongRectangle(w, s, e, n);
    }

    public static TileBounds convertFromLongRectangle(final LongRectangle rectangle)
    {
      return new TileBounds(rectangle.getMinX(), rectangle.getMinY(), rectangle.getMaxX(),
        rectangle.getMaxY());
    }

    public static LongRectangle convertToLongRectangle(final TileBounds bounds)
    {
      return new LongRectangle(bounds.w, bounds.s, bounds.e, bounds.n);
    }

    public void expand(final long tx, final long ty)
    {
      if (n < ty)
      {
        n = ty;
      }
      if (s > ty)
      {
        s = ty;
      }

      if (w > tx)
      {
        w = tx;
      }

      if (e < tx)
      {
        e = tx;
      }

    }

    public void expand(final long west, final long south, final long east, final long north)
    {
      if (n < north)
      {
        n = north;
      }
      if (s > south)
      {
        s = south;
      }

      if (w > west)
      {
        w = west;
      }

      if (e < east)
      {
        e = east;
      }

    }

    public void expand(final Tile t)
    {
      if (n < t.ty)
      {
        n = t.ty;
      }
      if (s > t.ty)
      {
        s = t.ty;
      }

      if (w > t.tx)
      {
        w = t.tx;
      }

      if (e < t.tx)
      {
        e = t.tx;
      }

    }

    public void expand(final TileBounds b)
    {
      if (n < b.n)
      {
        n = b.n;
      }
      if (s > b.s)
      {
        s = b.s;
      }

      if (w > b.w)
      {
        w = b.w;
      }

      if (e < b.e)
      {
        e = b.e;
      }
    }

    @Override
    public String toString()
    {
      return "TileBounds [w=" + w + ", s=" + s + ", e=" + e + ", n=" + n + "]";
    }

    public String toCommaString()
    {
      return w + "," + s + "," + e + "," + n;
    }

    public boolean contains(final long tx, final long ty) {
      return contains(new Tile(tx, ty), true);
    }

    public boolean contains(final long tx, final long ty, final boolean includeAdjacent) {
      return contains(new Tile(tx, ty), includeAdjacent);
    }

    public boolean contains(final Tile tile) {
      return contains(tile, true);
    }

    public boolean contains(final Tile tile, final boolean includeAdjacent) {
      if (includeAdjacent)
      {
        return (tile.tx >= w && tile.ty >= s && tile.tx <= e && tile.ty <= n);
      }
      else
      {
        return (tile.tx > w && tile.ty > s && tile.tx < e && tile.ty < n);
      }
    }

    public TileBounds intersection(final TileBounds b)
    {
      return intersection(b, true);
    }

    /**
     * If the two boundaries are adjacent, this would return true iff includeAdjacent is true
     *
     * @param b
     * @param includeAdjacent
     * @return
     */
    public TileBounds intersection(final TileBounds b, final boolean includeAdjacent)
    {

      final TileBounds intersectBounds = new TileBounds(Math.max(this.w, b.w), Math.max(this.s, b.s), Math
          .min(this.e, b.e), Math.min(this.n, b.n));
      if (includeAdjacent)
      {
        if (intersectBounds.w <= intersectBounds.e && intersectBounds.s <= intersectBounds.n)
        {
          return intersectBounds;
        }
      }
      else if (intersectBounds.w < intersectBounds.e && intersectBounds.s < intersectBounds.n)
      {
        return intersectBounds;
      }

      return null;
    }

  }

  // Tile 0, 0 is the lower-left corner of the world grid!
  // Pixel 0, 0 is the lower-left corner of the world grid!

  public static int MAXZOOMLEVEL = 22; // max zoom level (the highest X value

  // can be as an int)

  // Converts lat/lon bounds to the correct tile bounds for a zoom level
  public static TileBounds boundsToTile(final TMSUtils.Bounds bounds, final int zoom,
    final int tilesize)
  {
    final Tile ll = latLonToTile(bounds.s, bounds.w, zoom, tilesize, false);
    Tile ur = latLonToTile(bounds.n, bounds.e, zoom, tilesize, true);

    // this takes care of the case where the bounds is a vert or horiz "line" and also on the tile
    // boundaries.
    if (ur.tx < ll.tx)
    {
      ur = new Tile(ll.tx, ur.ty);
    }
    if (ur.ty < ll.ty)
    {
      ur = new Tile(ur.tx, ll.ty);
    }

    return new TileBounds(ll.tx, ll.ty, ur.tx, ur.ty);
  }

  // Converts lat/lon bounds to the correct tile bounds for a zoom level. Use this function
  // to compute the tile bounds when working with vector data because it does not use the
  // excludeEdge feature of the latLonToTile() function when computing the upper right tile.
  public static TileBounds boundsToTileExact(final TMSUtils.Bounds bounds, final int zoom,
    final int tilesize)
  {
    final Tile ll = latLonToTile(bounds.s, bounds.w, zoom, tilesize, false);
    Tile ur = latLonToTile(bounds.n, bounds.e, zoom, tilesize, false);

    // If the east coordinate is 180, the computed tx will be one larger than the max number
    // of tiles. Bring it back into range. Same for north.
    if (bounds.e >= 180.0)
    {
      ur = new Tile(ur.tx - 1, ur.ty);
    }
    if (bounds.n >= 90.0)
    {
      ur = new Tile(ur.tx, ur.ty - 1);
    }

    // this takes care of the case where the bounds is a vert or horiz "line" and also on the tile
    // boundaries.
    if (ur.tx < ll.tx)
    {
      ur = new Tile(ll.tx, ur.ty);
    }
    if (ur.ty < ll.ty)
    {
      ur = new Tile(ur.tx, ll.ty);
    }

    return new TileBounds(ll.tx, ll.ty, ur.tx, ur.ty);
  }

  public static Tile calculateTile(final Tile tile, final int srcZoom, final int dstZoom,
    final int tilesize)
  {
    final TMSUtils.Bounds bounds = TMSUtils.tileBounds(tile.tx, tile.ty, srcZoom, tilesize);

    return TMSUtils.latLonToTile(bounds.s, bounds.w, dstZoom, tilesize);
  }

  public static boolean isValidTile(final long tx, final long ty, final int zoomlevel)
  {
    return tx >= 0 && tx < (long) Math.pow(2, zoomlevel - 1) * 2 && ty >= 0 &&
      ty < (long) Math.pow(2, zoomlevel - 1);
  }

  // Converts lat/lon to pixel coordinates in given zoom of the EPSG:4326
  // pyramid
  public static Pixel latLonToPixels(final double lat, final double lon, final int zoom,
    final int tilesize)
  {
    final double res = resolution(zoom, tilesize);

    return new Pixel((long) ((180.0 + lon) / res), (long) ((90.0 + lat) / res));
  }

  // Converts lat/lon to pixel coordinates in given zoom of the EPSG:4326
  // pyramid in an upper-left as 0,0 coordinate grid
  public static Pixel latLonToPixelsUL(final double lat, final double lon, final int zoom,
    final int tilesize)
  {
    final Pixel p = latLonToPixels(lat, lon, zoom, tilesize);
    return new Pixel(p.px, (numYTiles(zoom) * tilesize) - p.py - 1);
    // final double res = resolution(zoom, tilesize);
    //
    // return new Pixel((long) ((180.0 + lon) / res), (long) ((90.0 - lat) / res));
  }

  // Returns the tile for zoom which covers given lat/lon coordinates"
  public static Tile latLonToTile(final double lat, final double lon, final int zoom,
    final int tilesize)
  {
    return latLonToTile(lat, lon, zoom, tilesize, false);
  }

  /**
   * Returns the pixel within a tile (where 0, 0 is anchored at the bottom left of the tile) for
   * zoom which covers given lat/lon coordinates
   */
  public static Pixel latLonToTilePixel(final double lat, final double lon, final long tx,
    final long ty, final int zoom, final int tilesize)
  {
    final Pixel p = latLonToPixels(lat, lon, zoom, tilesize);
    final Bounds b = tileBounds(tx, ty, zoom, tilesize);
    final Pixel ll = latLonToPixels(b.s, b.w, zoom, tilesize);
    return new Pixel(p.px - ll.px, p.py - ll.py);
  }

  /**
   * Returns the pixel within a tile (where 0, 0 is anchored at the top left of the tile) for zoom
   * which covers given lat/lon coordinates
   */
  public static Pixel latLonToTilePixelUL(final double lat, final double lon, final long tx,
    final long ty, final int zoom, final int tilesize)
  {
    final Pixel p = latLonToTilePixel(lat, lon, tx, ty, zoom, tilesize);
    return new Pixel(p.px, tilesize - p.py - 1);
  }

  // formulae taken from GDAL's gdal2tiles.py GlobalGeodetic() src code...

  public static long numXTiles(final int zoomlevel)
  {
    return (long) Math.pow(2, zoomlevel);
  }

  public static long numYTiles(final int zoomlevel)
  {
    return (long) Math.pow(2, zoomlevel - 1);
  }

  /**
   * Compute the worldwide tile in which the specified pixel resides. The pixel coordinates are
   * provided based on 0, 0 being bottom, left.
   * 
   * @param px
   * @param py
   * @param tilesize
   * @return
   */
  public static Tile pixelsToTile(final double px, final double py, final int tilesize)
  {
    return new Tile((long) (px / tilesize), (long) (py / tilesize));
  }

  /**
   * Compute the worldwide tile in which the specified pixel resides. The pixel coordinates are
   * provided based on 0, 0 being top, left.
   * 
   * @param px
   * @param py
   * @param zoom
   * @param tilesize
   * @return
   */
  public static Tile pixelsULToTile(final double px, final double py, final int zoom,
    final int tilesize)
  {
    final Tile tileFromBottom = pixelsToTile(px, py, tilesize);
    final Tile tileFromTop = new Tile(tileFromBottom.tx, numYTiles(zoom) - tileFromBottom.ty - 1);
    return tileFromTop;
    // long numYTiles = numYTiles(zoom);
    // long numXTiles = numXTiles(zoom);
    // long tilesFromTop = (long)(py / numXTiles);
    // long tileRow = numYTiles - tilesFromTop;
    // return new Tile((long) (px / tilesize), maxYTile - (long) (py / tilesize));
  }

  public static LatLon pixelToLatLon(final long px, final long py, final int zoom,
    final int tilesize)
  {
    final Tile tile = pixelsToTile(px, py, tilesize);
    final Bounds bounds = tileBounds(tile.tx, tile.ty, zoom, tilesize);
    final Pixel tilepx = latLonToPixels(bounds.s, bounds.w, zoom, tilesize);

    final double resolution = resolution(zoom, tilesize);
    final long pixelsFromTileLeft = px - tilepx.px;
    final long pixelsFromTileBottom = py - tilepx.py;
    final LatLon result = new LatLon(bounds.s + (pixelsFromTileBottom * resolution), bounds.w +
      (pixelsFromTileLeft * resolution));
    return result;
  }

  public static LatLon pixelToLatLonUL(final long px, final long py, final int zoom,
    final int tilesize)
  {
    final Tile tile = pixelsULToTile(px, py, zoom, tilesize);
    final Bounds bounds = tileBounds(tile.tx, tile.ty, zoom, tilesize);
    final Pixel tilepx = latLonToPixelsUL(bounds.n, bounds.w, zoom, tilesize);

    final double resolution = resolution(zoom, tilesize);
    final long pixelsFromTileLeft = px - tilepx.px;
    final long pixelsFromTileTop = py - tilepx.py;
    final LatLon result = new LatLon(bounds.n - (pixelsFromTileTop * resolution), bounds.w +
      (pixelsFromTileLeft * resolution));
    return result;
  }

  // Resolution (deg/pixel) for given zoom level (measured at Equator)"
  public static double resolution(final int zoom, final int tilesize)
  {
    if (zoom > 0)
    {
      return 180.0 / tilesize / Math.pow(2.0, zoom - 1);
    }

    return 0.0;
  }

  public static Bounds tileBounds(final long tx, final long ty, final int zoom, final int tilesize)
  {
    final double res = resolution(zoom, tilesize);

    return new Bounds(tx * tilesize * res - 180.0, // left/west (lon, x)
      ty * tilesize * res - 90.0, // lower/south (lat, y)
      (tx + 1) * tilesize * res - 180.0, // right/east (lon, x)
      (ty + 1) * tilesize * res - 90.0); // upper/north (lat, y)

  }

  // Converts lat/lon bounds to the correct tile bounds, in lat/lon for a zoom level
  public static Bounds tileBounds(final TMSUtils.Bounds bounds, final int zoom, final int tilesize)
  {
    final TMSUtils.TileBounds tb = TMSUtils.boundsToTile(bounds, zoom, tilesize);
    return TMSUtils.tileToBounds(tb, zoom, tilesize);
  }

  // Returns bounds of the given tile
  public static double[] tileBoundsArray(final long tx, final long ty, final int zoom,
    final int tilesize)
  {
    final Bounds b = tileBounds(tx, ty, zoom, tilesize);

    final double bounds[] = new double[4];

    bounds[0] = b.w;
    bounds[1] = b.s;
    bounds[2] = b.e;
    bounds[3] = b.n;

    return bounds;
  }

  public static Tile tileid(final long tileid, final int zoomlevel)
  {
    final long width = (long) Math.pow(2, zoomlevel);
    final long ty = tileid / width;
    final long tx = tileid - (ty * width);

    return new Tile(tx, ty);
  }

  public static long tileid(final long tx, final long ty, final int zoomlevel)
  {
    return (ty * (long) Math.pow(2, zoomlevel)) + tx;
  }

  public static long maxTileId(final int zoomlevel)
  {
    return numXTiles(zoomlevel) * numYTiles(zoomlevel) - 1;
  }

  // Returns bounds of the given tile in the SWNE form
  public static double[] tileSWNEBoundsArray(final long tx, final long ty, final int zoom,
    final int tilesize)
  {
    final Bounds b = tileBounds(tx, ty, zoom, tilesize);

    final double bounds[] = new double[4];

    bounds[0] = b.s;
    bounds[1] = b.w;
    bounds[2] = b.n;
    bounds[3] = b.e;

    return bounds;
  }

  // Converts tile bounds to the correct lat/lon bounds for a zoom level
  public static Bounds tileToBounds(final TMSUtils.TileBounds bounds, final int zoom,
    final int tilesize)
  {
    final Bounds ll = tileBounds(bounds.w, bounds.s, zoom, tilesize);
    final Bounds ur = tileBounds(bounds.e, bounds.n, zoom, tilesize);

    return new Bounds(ll.w, ll.s, ur.e, ur.n);
  }

  // Maximal scaledown zoom of the pyramid closest to the pixelSize."
  public static int zoomForPixelSize(final double pixelSize, final int tilesize)
  {
    final double pxep = pixelSize + 0.00000001; // pixelsize + epsilon
    for (int i = 1; i <= MAXZOOMLEVEL; i++)
    {
      if (pxep >= resolution(i, tilesize))
      {
        if (i > 0)
        {
          return i;
        }
      }
    }
    return 0; // We don't want to scale up
  }

  // Returns the tile for zoom which covers given lat/lon coordinates"
  private static Tile latLonToTile(final double lat, final double lon, final int zoom,
    final int tilesize, final boolean excludeEdge)
  {
    final Pixel p = latLonToPixels(lat, lon, zoom, tilesize);

    if (excludeEdge)
    {
      TMSUtils.Tile tile = TMSUtils.pixelsToTile(p.px, p.py, tilesize);
      final TMSUtils.Tile t = TMSUtils.pixelsToTile(p.px - 1, p.py - 1, tilesize);

      // lon is on an x tile boundary, so we'll move it to the left
      if (t.tx < tile.tx)
      {
        tile = new TMSUtils.Tile(t.tx, tile.ty);
      }

      // lat is on a y tile boundary, so we'll move it down
      if (t.ty < tile.ty)
      {
        tile = new TMSUtils.Tile(tile.tx, t.ty);
      }

      return tile;
    }

    return pixelsToTile(p.px, p.py, tilesize);
  }

}

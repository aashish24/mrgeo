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

import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.annotate.JsonIgnore;

import java.awt.geom.Rectangle2D;
import java.io.Serializable;

//NOTE: This class is json serialized, so there are @JsonIgnore annotations on the
//getters/setters that should not be automatically serialized

@SuppressWarnings("static-method")
public class LongRectangle implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private long minX, minY;
  private long maxX, maxY;
  private boolean set = false;

  public LongRectangle()
  {
    set = false;
  }

  public LongRectangle(final long minX, final long minY, final long maxX, final long maxY)
  {
    this.minX = minX;
    this.minY = minY;

    this.maxX = maxX;
    this.maxY = maxY;

    set = true;
  }
  
  public LongRectangle(LongRectangle copy)
  {
    this.minX = copy.minX;
    this.minY = copy.minY;

    this.maxX = copy.maxX;
    this.maxY = copy.maxY;

    set = true;
  }

  @SuppressWarnings("unused")
  static public void intersect(final LongRectangle src1, final LongRectangle src2,
      final LongRectangle dest)
  {
    throw new NotImplementedException();
  }

  @SuppressWarnings("unused")
  static void union(final LongRectangle src1, final LongRectangle src2, final LongRectangle dest)
  {
    throw new NotImplementedException();

  }

  public void add(final long x, final long y)
  {
    if (!set)
    {
      minX = maxX = x;
      minY = maxY = y;

      set = true;
    }
    else
    {
      if (x < minX)
      {
        minX = x;
      }
      if (x > maxX)
      {
        maxX = x;
      }

      if (y < minY)
      {
        minY = y;
      }
      if (y > maxY)
      {
        maxY = y;
      }
    }

  }

  public void add(final LongRectangle r)
  {
    add(r.minY, r.minY);
    add(r.maxX, r.maxY);
  }

  @Override
  public Object clone()
  {
    return new LongRectangle(minX, minY, maxX, maxY);
  }

  public boolean contains(final long x, final long y)
  {
    return outcode(x, y) == 0;
  }

  public boolean contains(final LongRectangle r)
  {
    if (outcode(r.minX, r.minY) == 0)
    {
      return outcode(r.maxX, r.maxY) == 0;
    }
    return false;
  }

  @SuppressWarnings("unused")
  public LongRectangle createIntersection(final LongRectangle r)
  {
    throw new NotImplementedException();
  }

  @SuppressWarnings("unused")
  public LongRectangle createUnion(final LongRectangle r)
  {
    throw new NotImplementedException();
  }

  @Override
  public boolean equals(final Object obj)
  {
    if (obj instanceof LongRectangle)
    {
      final LongRectangle r = (LongRectangle) obj;

      return (r.minX == minX && r.maxX == maxX && r.minY == minY && r.maxY == maxY);
    }

    return false;
  }

  @JsonIgnore
  public LongRectangle getBounds()
  {
    return (LongRectangle) clone();
  }
  
  @JsonIgnore
  public long getCenterX()
  {
    return minX + (maxX - minX) / 2;
  }

  @JsonIgnore
  public long getCenterY()
  {
    return minY + (maxY - minY) / 2;
  }
  
  @JsonIgnore
  public long getHeight()
  {
    //add 1 as maxY is inclusive
    return maxY - minY + 1;
  }

  public long getMaxX()
  {
    return maxX;
  }

  public long getMaxY()
  {
    return maxY;
  }

  public long getMinX()
  {
    return minX;
  }

  public long getMinY()
  {
    return minY;
  }

  @JsonIgnore
  public long getWidth()
  {
    //add 1 as maxX is inclusive
    return maxX - minX + 1;
  }

  public void grow(final long h, final long v)
  {
    minX -= h;
    maxX += h;

    minY -= v;
    maxY += v;
  }

  @SuppressWarnings("unused")
  public boolean intersects(final long srcMinX, final long srcMinY, final long srcMaxX, final long srcMaxY)
  {
    throw new NotImplementedException();
  }

  @SuppressWarnings("unused")
  public boolean intersects(final LongRectangle r)
  {
    throw new NotImplementedException();
  }

  @SuppressWarnings("unused")
  public boolean intersectsLine(final long x1, final long y1, final long x2, final long y2)
  {
    throw new NotImplementedException();
  }

  @JsonIgnore
  public boolean isEmpty()
  {
    return set;
  }

  public int outcode(final long x, final long y)
  {
    int outcode = 0;
    if (x < minX)
    {
      outcode |= Rectangle2D.OUT_LEFT;
    }
    else if (x > maxX)
    {
      outcode |= Rectangle2D.OUT_RIGHT;
    }

    if (y < minY)
    {
      outcode |= Rectangle2D.OUT_TOP;
    }
    else if (y > maxY)
    {
      outcode |= Rectangle2D.OUT_BOTTOM;
    }

    return outcode;
  }

  public void setMaxX(final long x)
  {
    maxX = x;
  }

  public void setMaxY(final long y)
  {
    maxY = y;
  }

  public void setMinX(final long x)
  {
    minX = x;
  }

  public void setMinY(final long y)
  {
    minY = y;
  }

  public void setRect(final long minX, final long minY, final long maxX, final long maxY)
  {
    this.minX = minX;
    this.minY = minY;

    this.maxX = maxX;
    this.maxY = maxY;

    set = true;
  }

  @Override
  public int hashCode()
  {
    return super.hashCode();
  }
  
  public static LongRectangle fromDelimitedString(final String rect)
  {
    String[] args = rect.split(",");
    if (args.length != 4)
    {
      throw new IllegalArgumentException("Delimited LongRectangle should be in the format of \"minx,miny,maxx,maxy\" (delimited by \",\") ");
    }
    
    return new LongRectangle(Long.parseLong(args[0]), Long.parseLong(args[1]),Long.parseLong(args[2]),Long.parseLong(args[3]));
  }

  public String toDelimitedString()
  {
    return String.format("%s,%s,%s,%s", minX, minY, maxX, maxY);
  }
  @Override
  public String toString()
  {
    return "Rectangle: (" + minX + ", " + minY + ") (" + maxX + ", " + maxY + ")";
  }
}

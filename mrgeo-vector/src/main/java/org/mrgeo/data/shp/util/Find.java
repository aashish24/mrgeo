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

package org.mrgeo.data.shp.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unchecked")
public class Find
{
  public static String CRLF = "\r\n";

  public static void main(String[] args)
  {
    try
    {
      switch (args.length)
      {
      case 3:
      {
        boolean dirs = false;
        if (args[2].equalsIgnoreCase("Y"))
          dirs = true;
        PrintWriter writer = null;
        if (!args[1].equalsIgnoreCase("null"))
          writer = new PrintWriter(new FileOutputStream(args[1]));
        Find f = new Find();
        f.search(new File(args[0]), writer, dirs, null);
        break;
      }
      case 4:
      {
        boolean dirs = false;
        if (args[2].equalsIgnoreCase("Y"))
          dirs = true;
        PrintWriter writer = null;
        if (!args[1].equalsIgnoreCase("null"))
          writer = new PrintWriter(new FileOutputStream(args[1]));
        Find f = new Find();
        f.search(new File(args[0]), writer, dirs, args[3]);
        break;
      }
      default:
      {
        System.out.println("USAGE: FIND <dir> <out> <Y|N> {regex}");
        System.exit(1);
      }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  @SuppressWarnings("rawtypes")
  private List list = null;

  public Find()
  {
  }

  @SuppressWarnings("rawtypes")
  public synchronized List search(File dir, boolean includedirectories, String regex)
      throws Exception
  {
    return search(dir, includedirectories, regex, false);
  }

  @SuppressWarnings("rawtypes")
  public synchronized List search(File dir, boolean includedirectories, String regex, boolean delete)
      throws Exception
  {
    if (!dir.isDirectory())
      throw new Exception("'" + dir.getCanonicalPath() + "' not a directory.");
    list = new ArrayList();
    searchInternal(dir, includedirectories, regex, delete);
    return list;
  }

  private void searchInternal(File dir, boolean dirs, String regex, boolean delete)
      throws IOException
  {
    File[] child = dir.listFiles();
    for (int i = 0; i < child.length; i++)
    {
      if (regex == null || (child[i].getCanonicalPath().endsWith(regex)))
      {
        if (child[i].isFile() || (dirs && child[i].isDirectory()))
        {
          list.add(child[i].getCanonicalPath());
          if (delete)
          {
            File temp = new File(child[i].getCanonicalPath());
            temp.delete();
          }
        }
      }
      if (child[i].isDirectory())
      {
        searchInternal(child[i], dirs, regex, delete);
      }
    }
  }

  @SuppressWarnings("rawtypes")
  private synchronized List search(File dir, PrintWriter writer, boolean dirs, String regex)
      throws Exception
  {
    if (!dir.isDirectory())
      throw new Exception("'" + dir.getCanonicalPath() + "' not a directory.");
    list = new ArrayList();
    searchInternal(dir, writer, dirs, regex);
    return list;
  }

  private synchronized void searchInternal(File dir, PrintWriter writer, boolean dirs, String regex)
      throws Exception
  {
    File[] child = dir.listFiles();
    for (int i = 0; i < child.length; i++)
    {
      if (regex == null || (child[i].getCanonicalPath().endsWith(regex)))
      {
        if (child[i].isFile() || (dirs && child[i].isDirectory()))
        {
          if (writer != null)
          {
            writer.write(child[i].getCanonicalPath() + CRLF);
          }
          else
          {
            System.out.println(child[i].getCanonicalPath());
          }
          list.add(child[i].getCanonicalPath());
        }
      }
      if (child[i].isDirectory())
      {
        searchInternal(child[i], writer, dirs, regex);
      }
    }
  }
}

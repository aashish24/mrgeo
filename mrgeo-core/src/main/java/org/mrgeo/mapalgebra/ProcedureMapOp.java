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

package org.mrgeo.mapalgebra;

import java.util.Vector;

import org.mrgeo.mapalgebra.parser.ParserAdapter;
import org.mrgeo.mapalgebra.parser.ParserNode;

/**
 * These MapOp's do not generate output on their own, they simply
 * perform some functionality. The executioner will set them as
 * execute listeners of each of their children so that it can
 * execute them after each of their children execute.
 * 
 * Subclasses should override the build method to perform
 * their duties. Note that method will be called by the executioner
 * once immediately after each of its children build.
 */
public abstract class ProcedureMapOp extends MapOp
{
  @Override
  public void addInput(MapOp n) throws IllegalArgumentException
  {
    _inputs.add(n);
  }

  @Override
  public Vector<ParserNode> processChildren(Vector<ParserNode> children, ParserAdapter parser)
  {
    return children;
  }
}

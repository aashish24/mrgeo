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

package org.mrgeo.job

import java.awt.image.WritableRaster
import java.net.URLClassLoader

import com.esotericsoftware.kryo.io.{Output, Input}
import com.esotericsoftware.kryo.{Serializer, KryoSerializable, Kryo}
import com.esotericsoftware.kryo.serializers.JavaSerializer
import org.apache.spark.graphx.{GraphKryoRegistrator, Edge}
import org.apache.spark.serializer.{KryoSerializer, KryoRegistrator}
import org.mrgeo.data.raster.RasterWritable
import org.mrgeo.data.tile.TileIdWritable
import org.mrgeo.spark.{BoundsSerializer, RasterWritableSerializer}
import org.mrgeo.utils.Bounds
import org.reflections.Reflections

import scala.collection.JavaConversions._

class KryoRegistrar extends KryoRegistrator
{
  override def registerClasses(kryo: Kryo) {

//    kryo.setReferences(false)

    kryo.register(classOf[TileIdWritable])
    kryo.register(classOf[RasterWritable], new RasterWritableSerializer)

    kryo.register(classOf[Bounds], new BoundsSerializer)

//    kryo.register(classOf[org.mrgeo.prototype.NND.Vertex])
//    kryo.register(classOf[org.mrgeo.prototype.NND.ChangedPointsSet])
//   kryo.register(classOf[org.mrgeo.prototype.NND.ChangedPointsPriorityQueue])

    kryo.register(classOf[Array[Long]])
//    kryo.register(classOf[org.mrgeo.prototype.CostDistance.VertexType])
//    kryo.register(classOf[Array[org.mrgeo.prototype.CostDistance.VertexType]])
//    kryo.register(classOf[org.mrgeo.prototype.CostDistance.ChangedPoints])
//    kryo.register(classOf[org.mrgeo.prototype.CostDistance.CostPoint])
    kryo.register(Class.forName("org.apache.spark.util.BoundedPriorityQueue"))

//    val r = new org.apache.spark.graphx.GraphKryoRegistrator
//    r.registerClasses(kryo)

    // set up a resource scanner
//    val reflections: Reflections = new Reflections("org.mrgeo")
//    val classes = reflections.getSubTypesOf(classOf[MrGeoSerializable])
//
//    for (clazz <- classes)
//    {
//      kryo.register(clazz)
//    }

//    // scala.collection.mutable.WrappedArray$ofRef
//    kryo.register(classOf[scala.collection.mutable.WrappedArray.ofRef[_]])
//
//    // Edge[]
//    kryo.register(classOf[Array[Edge[_]]])
//
//    // Edge<Object>
//    kryo.register(classOf[Edge[_]])
//    //org.apache.spark.graphx.Edge$mcI$sp
//    //kryo.register(classOf[org.apache.spark.graphx.Edge.mcI.sp[_]])
  }
}


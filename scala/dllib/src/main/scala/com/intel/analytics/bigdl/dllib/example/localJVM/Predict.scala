/*
 * Copyright 2016 The BigDL Authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intel.analytics.bigdl.example.localJVM
import com.intel.analytics.bigdl.dataset.image.{BytesToGreyImg, GreyImgNormalizer, GreyImgToSample}
import com.intel.analytics.bigdl.nn.Module
import com.intel.analytics.bigdl.optim.{LocalPredictor, Top1Accuracy, Validator}
import com.intel.analytics.bigdl.utils.Engine
import com.intel.analytics.bigdl.dataset.Sample
import org.apache.log4j.{Level, Logger}


import scala.collection.mutable.ArrayBuffer

object Predict {
  Logger.getLogger("org").setLevel(Level.ERROR)
  Logger.getLogger("akka").setLevel(Level.ERROR)
  Logger.getLogger("breeze").setLevel(Level.ERROR)
  Logger.getLogger("com.intel.analytics.bigdl.optim").setLevel(Level.INFO)

  import Utils._

  def main(args: Array[String]): Unit = {
    testParser.parse(args, new TestParams()).foreach { param =>

      System.setProperty("bigdl.localMode", "true")
      System.setProperty("bigdl.coreNumber", "4")
      Engine.init

      val validationData = param.folder + "/t10k-images-idx3-ubyte"
      val validationLabel = param.folder + "/t10k-labels-idx1-ubyte"

      val rawData = load(validationData, validationLabel)
      val iter = rawData.iterator
      val sampleIter = GreyImgToSample()(
        GreyImgNormalizer(trainMean, trainStd)(
          BytesToGreyImg(28, 28)(iter)))
      var samplesBuffer = ArrayBuffer[Sample[Float]]()
      while (sampleIter.hasNext) {
        val elem = sampleIter.next().clone()
        samplesBuffer += elem
      }
      val samples = samplesBuffer.toArray

      val model = Module.load[Float](param.model)
      val weightsBias = LocalPredictor.getAndClearWeightBias(model.parameters())
      val predictor = LocalPredictor(model, weightsBias)

      val result = predictor.predict(samples)
      val result_class = predictor.predictClass(samples)
      result_class.foreach(r => println(s"${r}"))
    }
  }
}

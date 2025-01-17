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
package com.intel.analytics.bigdl.dllib.nn

import com.intel.analytics.bigdl.dllib.tensor.Tensor
import com.intel.analytics.bigdl.dllib.utils.serializer.ModuleSerializationTest
import com.intel.analytics.bigdl.dllib.utils.{T, Table}
import org.scalatest.{FlatSpec, Matchers}

import scala.util.Random

class AttentionSpec  extends FlatSpec with Matchers {


  val inputX : Tensor[Float] = Tensor(T(
    T(T( 2.43651805, -0.91763462, -0.79225763, -1.60945293, 1.29811144,
      -3.45230805, 2.61721765, -1.14181035),
      T( 0.47855864, -0.37405556, 2.19316191, -3.09021106, -0.48362581,
      -0.57608153, 1.70065416, -1.6498369),
      T(-0.25864231, -1.31678763, 0.06332062, 0.87422282, -1.65092877,
      1.71708556, 1.35238608, 0.75374151)),
      T(T( 1.35128392, -1.02559179, -0.18433534, -1.40365415, -0.40183212,
      0.7955332, -1.03749113, -0.59513029),
      T(-1.03075905, -1.26780846, -1.0068692, -0.0189969, -1.67596552,
      0.35162355, 2.48970327, 1.11306624),
      T(-0.28775333, -1.33144345, -1.12073744, 2.5386819, 0.07621163,
        -0.95549347, 0.28637323, 3.1503827))))

  val inputY : Tensor[Float] = inputX.clone()

  val inputBias : Tensor[Float] = Tensor(T(
      T(T(T( 0.06007948, 0.30860155, 0.15008516),
        T(-0.17612492, -0.5712591, -0.17467136),
        T(-0.10444712, 0.2933116, 0.41949171)),

      T(T( 0.46555104, 0.14279366, 0.44257058),
        T(-0.37719897, 0.62643408, 0.25646491),
        T(-0.14904642, 0.24425907, -0.03778586)),

      T(T( 0.56581469, 0.75990841, 1.0927877),
        T(-0.69824817, -0.7220569, -0.25223293),
        T( 0.08001853, 0.43808446, 0.15781747)),

      T(T(-1.01110061, -0.15310201, 0.41398732),
        T( 0.11504737, 0.38100559, -0.11116407),
        T(-0.10037903, 0.0932807, 0.20502582))),


      T(T(T( 0.09914986, 0.05950432, -0.33533114),
        T( 0.18878189, 0.06091064, 0.56474195),
        T( 0.59945894, 0.09257821, -0.18764248)),

        T(T(-0.3193652, 0.21174718, 0.03867003),
          T(-0.17192684, 0.02179843, -0.31000042),
          T( 0.34901602, -0.22356428, 0.61225385)),

        T(T( 0.20174582, 0.29678926, -0.54745592),
          T( 0.08469122, 0.37027823, -0.4768503),
          T(-0.13310925, 0.01630727, -0.68655866)),

        T(T( 0.1575797, 0.42308032, -0.42975797),
          T( 0.17527299, -0.65614171, -0.01934775),
        T(-0.80788618, 0.56070885, 0.20445027)))))

  val outputExpected : Tensor[Float] = Tensor[Float](
      T(T(T(-1.3136294, -2.3003874, -1.8152907, -1.2017354, -0.30692226,
        0.7014533, -0.48527908, 1.2008696),
        T(-0.80872196, -1.8982431, -3.7982664, -1.5464412, -2.128195,
        0.921171, -0.514083, 0.7338527),
        T(-0.6878601, -2.1513283, 0.06790769, -1.8393003, 0.18802914,
        0.32452816, -0.63091534, -1.6523509)),
        T(T( 1.0437143, -0.5991106, -1.8075838, -1.3340924, -1.9741716,
        2.2952275, -0.7159063, -0.56103015),
        T( 0.12556843, -1.1148375, -1.1061573, -0.76132846, -1.6811743,
        1.434186, -0.5047271, 0.513326),
        T(-0.4049306, -1.1523883, -1.3060606, -0.78532106, -1.1865962,
        1.1612856, -0.4876815, 0.57233703))))

  val weights: Table = T(
    "q" -> Tensor[Float](
        T(T(-0.372805, -0.57580054, -0.16542524, -0.29865405, 0.35401803, 0.15270126,
        -0.54465574, 0.15932709),
        T( 0.24691772, 0.30155098, 0.4186222, 0.2167002, 0.30048692, 0.27184665,
        0.39705545, -0.23575303),
        T( 0.00388521, 0.20807374, -0.378344, -0.30214158, -0.34708476, 0.04026955,
          -0.55643994, -0.5794907),
        T( 0.49563867, -0.20237926, -0.46280175, 0.28509408, 0.54167503, -0.3143271,
          -0.12728554, 0.38375044),
        T( 0.32280642, -0.5431511, 0.09327781, 0.26422644, -0.1516226, -0.592104,
          -0.4920348, -0.06154263),
        T(-0.3427992, -0.28234676, 0.60987645, -0.04226011, -0.4681016, -0.1708524,
        0.14569217, -0.08339447),
        T( 0.22560287, 0.35561, -0.50295657, 0.13627058, -0.3947954, 0.5856554,
          -0.4278072, -0.20018426),
        T(-0.262408, -0.21194538, -0.5646615, -0.50292665, -0.47206333, -0.5250014,
        0.26842934, 0.28272492))),
    "k" -> Tensor[Float](T(
        T(-0.343275, -0.5302577, 0.22225219, 0.22917205, -0.35248256, -0.52561647,
        -0.49496183, 0.19416988),
        T( 0.59556, 0.15709078, -0.5260543, 0.3003326, -0.4924144, 0.19770503,
        0.18886334, -0.4183287),
        T(-0.14076799, 0.20558482, -0.44356102, 0.3057044, -0.0961917, -0.41457063,
          -0.25426582, -0.43088654),
        T( 0.00211596, 0.5313905, 0.38138926, -0.53933024, 0.25935173, -0.4545771,
          -0.5513677, -0.42323098),
        T( 0.60221463, 0.46009654, -0.3742085, 0.30695522, -0.14824063, 0.08633447,
        0.5154777, -0.31166738),
        T( 0.5757794, -0.00155389, -0.27291873, 0.01211369, 0.10273433, -0.5679398,
          -0.4605189, -0.60379565),
        T(-0.2338264, -0.40447962, -0.20583275, 0.12039971, -0.4886889, -0.26302016,
        0.56051654, 0.0246914),
        T(-0.0083527, 0.07543635, 0.6011241, 0.5061092, -0.17393082, -0.02609855,
          -0.03866196, -0.47378802))),
    "v" -> Tensor[Float](
        T(T(-0.27888697, -0.3508993, 0.00061786, -0.05899942, -0.4096707, -0.59099805,
        0.00982529, 0.05359054),
        T( 0.3683961, -0.05546927, -0.2827503, 0.43347543, 0.1822511, -0.16377908,
          -0.5162845, -0.43161902),
        T( 0.46889406, 0.59701246, 0.48150903, 0.4334857, 0.486095, 0.53306824,
        0.27221018, 0.5941089),
        T( 0.12607813, -0.5313994, -0.57173353, -0.12448379, -0.11713088, -0.4439688,
          -0.527298, -0.37749383),
        T(-0.3919587, 0.05043119, 0.18434244, -0.01674193, -0.20570382, -0.21749035,
          -0.2891266, 0.12637317),
        T( 0.52648765, -0.07314765, 0.48385805, -0.03910315, 0.22911525, 0.01771665,
          -0.02246779, -0.40268806),
        T(-0.54250515, -0.31025118, -0.03211451, -0.12393585, -0.4777977, 0.18552327,
          -0.3151345, -0.5560428),
        T( 0.38067168, 0.45435983, 0.46077865, -0.10283256, -0.3396571, 0.26476836,
          -0.25029647, -0.5956288))),
    "output_transform" -> Tensor[Float](
        T(T(-0.22421107, 0.350811, 0.05354661, 0.6110292, -0.3909106, -0.5944199,
        0.10645795, 0.57871825),
        T(-0.5649649, -0.23917922, 0.3865885, 0.44473797, 0.29352474, -0.50426036,
          -0.3379699, 0.00927532),
        T(-0.37847292, -0.4825884, -0.05675334, -0.01127535, 0.08974767, -0.06169283,
        0.15506953, -0.02398986),
        T(-0.34070057, 0.12476408, 0.5375441, 0.2504276, 0.5667407, -0.599416,
        0.09187245, 0.5948525),
        T( 0.16609788, 0.55267304, 0.54386073, 0.18300432, 0.59399253, 0.02860391,
        0.26716715, -0.14422473),
        T( 0.41911787, -0.19523674, 0.4970067, 0.15865183, -0.46091762, 0.5183502,
          -0.2546733, 0.37238264),
        T(-0.23758182, 0.2648332, 0.14880872, -0.41371652, -0.52281517, 0.3087402,
          -0.4304452, -0.12153107),
        T( 0.02987367, 0.01645315, 0.58394355, 0.16796988, 0.23654258, -0.50470173,
        0.07536042, -0.5896087))))

  val gradWeightsExpected = T(
    "q" -> Tensor[Float](
        T(T(-2.8356311, -2.4773571, 1.4626868, -0.6618118, -4.628455,
        5.1103063, -0.08714008, 0.17469034),
        T(9.355147, 8.278282, -2.5292795, 1.2974377, 4.203867,
          -3.8572924, -6.996191, 2.9874544),
        T(-1.8837652, -1.9733994, 6.421815, -4.012224, 1.106437,
          -0.727377, -4.41868, 1.7946866),
        T(1.5535867, 1.5127493, -9.956939, 5.752798, 3.6158886,
          -3.8260145, 2.7087438, -0.7301127),
        T(7.9238024, 7.238671, -2.934513, 1.6450198, 0.14482632,
        0.48197156, -3.8312237, 2.020553),
        T(-4.122905, -3.6462588, -0.68954813, 0.63985145, 4.7653265,
          -5.5613956, 0.35840988, -0.5148314),
        T(-12.359057, -11.95616, 6.206677, -4.027071, -7.648021,
        8.011374, 3.4324074, -1.9123353),
        T(-0.78880894, -0.2865741, -4.623139, 2.743837, 1.5203985,
          -2.1541567, 7.4303446, -2.7613451))),
    "k" -> Tensor[Float](
        T(T(-4.8302217, 7.3123174, 1.6504537, 0.9700441, 2.3480177,
        16.103554, -2.2903576, -8.238806),
        T(-0.32502297, 0.57444924, 0.14206292, 0.68621343, 0.9674345,
          -3.0744767, -0.19433197, -0.7038229),
        T(3.8866534, -2.5272706, 0.31954706, 1.4289378, 4.1573987,
          -18.83666, -0.6341135, -2.324824),
        T(3.2729623, -4.4220643, -0.4427252, -3.3440435, -4.4044204,
        4.3912477, 1.4300863, 4.571388),
        T( -5.8958073, 6.9818015, 1.2565684, 0.95525885, 0.454724,
        12.518224, -1.0987915, -4.2782307),
        T( 12.0007305, -10.336534, -0.4299186, -1.6812495, 3.2675288,
          -18.79188, -0.5088088, -1.5055372),
        T( -5.3735647, 0.367167, -2.2015429, 0.1204319, -6.43669,
        4.354989, 3.1718657, 11.734762),
        T(  2.4331138, -4.1149745, -0.68130356, -2.264487, -3.9137423,
          -1.6834034, 1.7719116, 5.8460846))),
    "v" -> Tensor[Float](
        T(T( -2.5827155, 1.3582011, 2.75234, -5.447733, -19.511236,
        -2.1552057, 1.0299364, 2.464095),
        T(  9.669751, 9.907997, -2.63263, 16.046946, 17.104738,
          -3.7717283, -7.977087, 16.455389),
        T(  1.4447857, 4.726976, 6.355788, -0.3261361, 8.890461,
          -2.7009814, -2.7735417, 4.597517),
        T(  3.7391737, -2.652472, -10.343583, 3.4428883, 17.676163,
        1.3461249, -0.89123046, -5.0282454),
        T(  6.218705, 8.004267, -2.7794306, 7.9928293, -2.384931,
          -4.3906345, -6.2733536, 17.570385),
        T(  1.4547603, -2.6338978, -0.1248658, -0.18013573, 26.240273,
        3.4490743, 2.5249462, -14.606245),
        T(-13.57461, -9.627181, 5.8085995, -9.885314, -30.600082,
        1.5869138, 7.419276, -13.439232),
        T( -2.4042547, -8.270382, -5.8752723, -7.9662743, 4.8289614,
        4.279446, 3.5127287, -10.48413))),
    "output_transform" -> Tensor[Float](
        T(T( 2.8776762e+00, 9.0993910e+00, 4.4741273e+00, 7.2155852e+00,
        2.1099086e+00, -3.7240963e+00, 2.8145332e+00, 2.2895024e+00),
        T(-1.4586559e-02, 2.0312614e+00, 2.9373944e+00, 2.0424197e+00,
        2.5295980e+00, -2.0257745e+00, 8.9276981e-01, 2.4247412e-01),
        T(-4.7104540e+00, -1.0320190e+01, -7.1374278e+00, -6.6327205e+00,
          -3.4700847e+00, 3.6055198e+00, -2.6972079e+00, 1.5516624e+00),
        T(-3.3305209e+00, -1.6513400e+00, 3.6665101e+00, 6.0572940e-01,
        5.1936049e+00, -4.1640496e+00, 7.2286522e-01, 1.1406650e-01),
        T( 5.9342289e+00, 1.7988518e+01, 1.9854633e+01, 1.3903372e+01,
        1.1869378e+01, -9.9151783e+00, 5.5996485e+00, -2.1639798e+00),
        T( 1.5641862e+00, 1.1334980e+00, 2.3218460e+00, 6.1690319e-01,
          -2.1347943e-01, 1.2895620e+00, -1.5324564e-01, -5.2227557e-01),
        T(-5.4851645e-01, -3.2748022e+00, -1.7621651e+00, -2.8479974e+00,
          -1.6886721e+00, 2.1033278e+00, -1.2398324e+00, -1.1378943e+00),
        T( 2.3364418e+00, 7.2459126e+00, 1.4526119e+01, 4.9446974e+00,
        9.3395023e+00, -7.0863304e+00, 2.4963975e+00, -6.7891836e+00)))
  )
  "attention layer" should "work correctly" in {
    // compare with tensorflow 1.13.1
    val attention = new Attention[Float](8, 4, 1.0f)

    val paramsTable = attention.getParametersTable()
    val w1 = weights.get[Tensor[Float]]("q").get
    val w2 = weights.get[Tensor[Float]]("k").get
    val w3 = weights.get[Tensor[Float]]("v").get
    val w4 = weights.get[Tensor[Float]]("output_transform").get
    for (i <- paramsTable.keySet) {
      val params = paramsTable.get[Table](i).get.get[Tensor[Float]]("weight").get
      if (i.toString contains "_q") {
        params.copy(w1.t())
      } else if (i.toString contains "_k") {
        params.copy(w2.t())
      } else if (i.toString contains "_v") {
        params.copy(w3.t())
      } else if (i.toString contains "_output_transform") {
        params.copy(w4.t())
      }
    }

    val output = attention.forward(T(inputX, inputY, inputBias))
    val gradInput = attention.backward(T(inputX, inputY, inputBias), output)

    output should  be(outputExpected)
    // gradInput should be(gradInputExpected)

    val gw1 = gradWeightsExpected.get[Tensor[Float]]("q").get
    val gw2 = gradWeightsExpected.get[Tensor[Float]]("k").get
    val gw3 = gradWeightsExpected.get[Tensor[Float]]("v").get
    val gw4 = gradWeightsExpected.get[Tensor[Float]]("output_transform").get
    for (i <- paramsTable.keySet) {
      val params = paramsTable.get[Table](i).get.get[Tensor[Float]]("gradWeight").get
      if (i.toString contains "_q") params should be(gw1.t())
      if (i.toString contains "_k") params should be(gw2.t())
      if (i.toString contains "_v") params should be(gw3.t())
      if (i.toString contains "_output_transform") params should be(gw4.t())
    }
  }
}

class AttentionSerialTest extends ModuleSerializationTest {
  override def test(): Unit = {
    val attention = new Attention[Float](8, 4, 1.0f).setName("attention")
    val inputX = Tensor[Float](2, 3, 8).apply1(_ => Random.nextFloat())
    val inputY = inputX.clone()
    val inputBias = Tensor[Float](2, 4, 3, 3).apply1(_ => Random.nextFloat())
    runSerializationTest(attention, T(inputX, inputY, inputBias))
  }
}

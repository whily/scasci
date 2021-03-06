/**
 * Test cases for Vec.scala.
 *
 * @author  Yujian Zhang <yujian{dot}zhang[at]gmail(dot)com>
 *
 * License:
 *   GNU General Public License v2
 *   http://www.gnu.org/licenses/gpl-2.0.html
 * Copyright (C) 2013 Yujian Zhang
 */

import net.whily.scasci.math.linalg.Vec3
import org.scalatest.Matchers
import org.scalatest.FunSpec

class Vec3Spec extends FunSpec with Matchers {
  describe("In class Vec3") {
    it("fill") {
      val x = Vec3(1.0, 2.0, 3.0)
      x.fill(4.0)
      x should be (Vec3(4.0, 4.0, 4.0))
    }

    it("unary -") {
      -Vec3(1.0, 2.0, 3.0) should be (Vec3(-1.0, -2.0, -3.0))
    }

    it("plus +") {
      Vec3(1.0, 2.0, 3.0) + Vec3(4.0, 5.0, 6.0) should be (Vec3(5.0, 7.0, 9.0))
    }

    it("minus -") {
      Vec3(5.0, 7.0, 9.0) - Vec3(1.0, 2.0, 3.0) should be (Vec3(4.0, 5.0, 6.0))
    }

    it("times *") {
      Vec3(1.0, 2.0, 3.0) * 4.0 should be (Vec3(4.0, 8.0, 12.0))
    }

    it("div /") {
      Vec3(2.0, 4.0, 6.0) / 2.0 should be (Vec3(1.0, 2.0, 3.0))
    }

    it("dot prodcut") {
      Vec3(1.0, 3.0, -5.0) ⋅ Vec3(4.0, -2.0, -1.0) should be (3.0)
    }

    it("cross product") {
      Vec3(2.0, 3.0, 4.0) × Vec3(5.0, 6.0, 7.0) should be (Vec3(-3.0, 6.0, -3.0))
    }

    it("norm") {
      Vec3(1.0, 2.0, 2.0).norm should be (3.0)
    }
  }
}

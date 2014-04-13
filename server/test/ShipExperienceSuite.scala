import org.scalatest.FunSuite
import util.ShipExperience

/**
 *
 * @author
 * Date: 14/04/14.
 */
class ShipExperienceSuite extends FunSuite {
  test("Lv1 Experience") {
    assert(ShipExperience.diff(1) === 0)
    assert(ShipExperience.sum(1) === 0)
  }
  test("Lv51 Experience") {
    assert(ShipExperience.diff(51) === 5000)
    assert(ShipExperience.sum(51) === 127500)
  }
  test("Lv52 Experience") {
    assert(ShipExperience.diff(52) === 5200)
    assert(ShipExperience.sum(52) === 132700)
  }
  test("Lv99 Experience") {
    assert(ShipExperience.diff(99) === 148500)
    assert(ShipExperience.sum(99) === 1000000)
  }
  test("Lv150 Experience") {
    assert(ShipExperience.diff(150) === 195000)
    assert(ShipExperience.sum(150) === 4360000)
  }
}

package wp.pigeonsapp2

import wp.Spec
import wp.pigeonapp2.Model

class ModelSpec extends wp.Spec {
  import Model._

  "Model should work" in {

    pigeons.now mustBe allPigeons

    removePigeon(10) mustBe None
    pigeons.now mustBe allPigeons

    removePigeon(lucyId) mustBe Some(lu)
    pigeons.now mustBe allPigeons - lucyId

    removePigeon(lucyId) mustBe None
    pigeons.now mustBe allPigeons - lucyId

    addPigeons(lucy, lucy, lucy) mustBe Seq(6,7,8)
    pigeons.now mustBe allPigeons - lucyId + (6 -> lucy)+ (7 -> lucy)+ (8 -> lucy)

    pigeons() = allPigeons
    updatePigeon(bobId, _.copy(age=10))
    pigeons.now mustBe allPigeons.updated(bobId, Pigeon("Bob", 10, Flying(Shitting)))

    pigeons() = allPigeons
    updatePigeon(100, _.copy(age=10))
    pigeons.now mustBe allPigeons

  }

  lazy val lu@(lucyId, lucy) = 1 -> Pigeon("Lucy", 1, Landing)
  lazy val bu@(bobId, bob) = 2 -> Pigeon("Bob", 2, Flying(Shitting))
  lazy val ca@(carmenId, carmen) = 3 -> Pigeon("Carmen", 2, Starting)
  lazy val gr@(grahamId, graham) = 4 -> Pigeon("Graham", 2, Standing(Pecking))
  lazy val al@(allId, all) = 5 -> Pigeon("All", 2, Landing)

  lazy val allPigeons = Map(
    lucyId -> lucy,
    bobId -> bob,
    carmenId -> carmen,
    grahamId -> graham,
    allId -> all
  )

}

/*
 * Copyright 2019 HM Revenue & Customs
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

package uk.gov.hmrc.ia.repository

import javax.inject.Inject
import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.{Json, OFormat}
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.ia.support.ItSpec

class TestRepo @Inject() (reactiveMongoComponent: ReactiveMongoComponent)
  extends Repo[Row, String]("temp-collection", reactiveMongoComponent) {
}

case class Row(_id: String = "1", utr: String)

object Row {
  implicit val utrFormat: OFormat[Row] = Json.format[Row]
}

class RepoSpec extends ItSpec with BeforeAndAfterEach {

  val testRepo = fakeApplication().injector.instanceOf[TestRepo]

  override def afterEach(): Unit = {
    testRepo.collection.drop(true).futureValue
  }

  "repo" should {

    "insert a record" in {
      testRepo.insert(Row(utr = "ABC")).futureValue
      val res = testRepo.findById("1").futureValue
      res.get.utr shouldBe "ABC"
    }

  }

}

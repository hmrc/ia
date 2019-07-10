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

package uk.gov.hmrc.ia.controllers

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.Json
import uk.gov.hmrc.ia.support.{ItSpec, TestConnector}

class IaUtrsSpec extends ItSpec with BeforeAndAfterEach {

  val connector = fakeApplication().injector.instanceOf[TestConnector]

  private val utrListJson = Json.arr(
    Json.obj("utr" -> "1234567890"),
    Json.obj("utr" -> "1234567891")
  )

  override def beforeEach(): Unit = {
    connector.drop().futureValue
    connector.setDb("DB1").futureValue
  }

  "client" should {

    "be able to upload the utrs and get how many where updated back" in {
      val resultPost = connector.uploadUtrs(utrListJson).futureValue
      resultPost.status shouldBe 200
      resultPost.body shouldBe "2"
    }

    "be able to upload a single urt" in {
      val resultPost = connector.uploadUtr("999999999").futureValue
      resultPost.status shouldBe 200
    }

    "be able to check if a utr is in the db" in {
      connector.uploadUtr("1234567890").futureValue
      val findResult = connector.find("1234567890").futureValue
      findResult.status shouldBe 200
    }

    "be able to check if a utr not in the db" in {
      connector.uploadUtrs(utrListJson).futureValue
      val findResult = connector.find("9999999999").futureValue
      findResult.status shouldBe 204
    }

    "be able to drop the db" in {
      connector.uploadUtrs(utrListJson).futureValue
      connector.drop().futureValue
      val findResult = connector.find("1234567890").futureValue
      findResult.status shouldBe 204
    }

    "be able see to upload to the inactive db" in {
      connector.uploadUtrs(utrListJson).futureValue

      val countResult = connector.count().futureValue
      countResult.status shouldBe 200
      countResult.body shouldBe "DbCount(SSTTP is currently pointed to DataBase DB1,count DataBase 1 is 0,count DataBase 2 is 2)"
    }

    "be able to switch to another db and drop the other one " in {
      connector.uploadUtrs(utrListJson).futureValue
      connector.switch().futureValue
      val countResult = connector.count().futureValue
      countResult.status shouldBe 200
      countResult.body shouldBe "DbCount(SSTTP is currently pointed to DataBase DB2,count DataBase 1 is 0,count DataBase 2 is 2)"
    }
  }
}

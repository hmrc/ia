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

package uk.gov.hmrc.ia.service

import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar
import uk.gov.hmrc.ia.domain.CurrentActiveDbs
import uk.gov.hmrc.ia.repository.{ActiveRepo, ValidUtrRepoOne, ValidUtrRepoTwo}
import uk.gov.hmrc.ia.support.TestData._
import uk.gov.hmrc.ia.support.{ItSpec, TestConnector}

class GreenUtrServiceSpec extends ItSpec with MockitoSugar with BeforeAndAfterEach {

  val connector = fakeApplication().injector.instanceOf[TestConnector]
  val validUtrRepoOne = fakeApplication().injector.instanceOf[ValidUtrRepoOne]
  val validUtrRepoTwo = fakeApplication().injector.instanceOf[ValidUtrRepoTwo]
  val activeRepo = fakeApplication().injector.instanceOf[ActiveRepo]
  val greenUtrService = new GreenUtrService(validUtrRepoOne, validUtrRepoTwo, activeRepo)

  override def beforeEach(): Unit = {
    connector.drop().futureValue
  }

  class BulkInsertRejected extends Exception("No objects inserted. Error converting some or all to JSON")

  "The  GreenUtrService should insert Utrs into the inactive db" in {
    val result: Unit = greenUtrService.uploadBulkInActiveDb(validUtrs).futureValue
    result shouldBe (())
  }
  "The  GreenUtrService should insert Utrs into the active db" in {
    val result: Unit = greenUtrService.uploadActiveDb(validUtrs).futureValue
    result shouldBe (())
  }

  "return true if the item is in the db in" in {
    greenUtrService.uploadActiveDb(validUtrs).futureValue
    val result = greenUtrService.isGreenUtr("123456789").futureValue
    result shouldBe true
  }

  "return false if the item is in the db in" in {
    val result = greenUtrService.isGreenUtr("1234567890").futureValue
    result shouldBe false
  }

  "return switchDB should switch the db and drop the other one " in {
    greenUtrService.setDb(CurrentActiveDbs.DB1).futureValue
    greenUtrService.uploadBulkInActiveDb(validUtrs).futureValue
    val result = greenUtrService.isGreenUtr("123456789").futureValue
    result shouldBe false
    greenUtrService.switchDB.futureValue
    val result2 = greenUtrService.isGreenUtr("123456789").futureValue
    result2 shouldBe true
  }
}

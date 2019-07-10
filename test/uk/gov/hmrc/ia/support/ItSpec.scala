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

package uk.gov.hmrc.ia.support

import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{AppendedClues, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.ExecutionContext

trait ItSpec extends WordSpec with ScalaFutures with IntegrationPatience with Matchers with AppendedClues
  with GuiceOneServerPerSuite {
  override lazy val port: Int = 8051

  def httpClient = fakeApplication().injector.instanceOf[HttpClient]
  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  implicit val hc: HeaderCarrier = HeaderCarrier()

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(Map[String, Any](
        "mongodb.uri " -> "mongodb://localhost:27017/ia-it"
      )).build()

}

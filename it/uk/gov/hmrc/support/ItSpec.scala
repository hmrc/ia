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

package uk.gov.hmrc.support

import akka.actor.ActorSystem
import com.typesafe.config.Config
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{AppendedClues, Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneServerPerSuite
import play.api.Application
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.ws.WSClient
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.hooks.HttpHook
import uk.gov.hmrc.play.bootstrap.http.HttpClient
import uk.gov.hmrc.play.http.ws.WSHttp

import scala.concurrent.ExecutionContext

trait ItSpec extends WordSpec with ScalaFutures with IntegrationPatience with Matchers with AppendedClues
  with GuiceOneServerPerSuite {
  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(
      ).build()

  override lazy val port: Int = 8051
  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global


  implicit val hc: HeaderCarrier = HeaderCarrier()

  val httpClient: HttpClient = new HttpClient with WSHttp {
    override def wsClient: WSClient = app.injector.instanceOf(classOf[WSClient])

    override val hooks: Seq[HttpHook] = Nil

    override protected def configuration: Option[Config] = None

    override protected def actorSystem: ActorSystem = app.injector.instanceOf[ActorSystem]
  }
}

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

import javax.inject.{Inject, Singleton}
import play.api.libs.json.JsValue
import uk.gov.hmrc.http.{HeaderCarrier, HttpResponse}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class TestConnector @Inject() (httpClient: HttpClient)(implicit executionContext: ExecutionContext) {

  private val iaBaseUrl = "http://localhost:8051"
  private val uploadUrl: String = iaBaseUrl + "/ia/upload"
  private val switchUrl: String = iaBaseUrl + "/ia/switch"
  private val countUrl: String = iaBaseUrl + "/ia/count"
  private val dropUrl: String = iaBaseUrl + "/ia/drop-all"
  private val setDbUrl: String = iaBaseUrl + "/ia/set/"
  private val findUrlBase: String = iaBaseUrl + "/ia/"

  def uploadUtrs(request: JsValue)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClient.POST(uploadUrl, request)
  }

  def uploadUtr(utr: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClient.POSTEmpty(uploadUrl + s"/$utr")
  }

  def switch()(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClient.POSTEmpty(switchUrl)
  }

  def count()(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClient.GET(countUrl)
  }

  def drop()(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClient.POSTEmpty(dropUrl)
  }

  def setDb(dbName: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClient.POSTEmpty(setDbUrl + dbName)
  }

  def find(utr: String)(implicit hc: HeaderCarrier): Future[HttpResponse] = {
    httpClient.GET(findUrlBase + utr)
  }
}

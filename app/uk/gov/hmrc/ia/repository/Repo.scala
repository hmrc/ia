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

import play.api.libs.json._
import play.modules.reactivemongo.ReactiveMongoComponent
import reactivemongo.api.commands.WriteResult
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.mongo.ReactiveRepository

import scala.concurrent.{ExecutionContext, Future}

abstract class Repo[A, ID](
    collectionName:         String,
    reactiveMongoComponent: ReactiveMongoComponent)(implicit manifest: Manifest[A],
                                                    mid:          Manifest[ID],
                                                    domainFormat: OFormat[A],
                                                    idFormat:     Format[ID])
  extends ReactiveRepository[A, ID](
    collectionName,
    reactiveMongoComponent.mongoConnector.db,
    domainFormat,
    idFormat) {

  implicit val f: OWrites[JsObject] = new OWrites[JsObject] {
    override def writes(o: JsObject): JsObject = o
  }

  protected implicit class WriteResultChecker(future: Future[WriteResult])(implicit hc: HeaderCarrier, ec: ExecutionContext) {
    def checkResult: Future[Unit] = future.map { writeResult =>
      if (hasAnyConcerns(writeResult)) throw new RuntimeException(writeResult.toString)
      else ()
    }
  }

  private def hasAnyConcerns(writeResult: WriteResult): Boolean = !writeResult.ok || writeResult.writeErrors.nonEmpty || writeResult.writeConcernError.isDefined

}


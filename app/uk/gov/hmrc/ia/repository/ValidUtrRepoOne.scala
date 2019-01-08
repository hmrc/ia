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
import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoComponent
import uk.gov.hmrc.ia.domain.CurrentActiveDbs._
import uk.gov.hmrc.ia.domain.{ActiveDb, CurrentActiveDb, GreenUtr}

import scala.concurrent.{ExecutionContext, Future}
import scala.util.Failure


class ValidUtrRepoOne @Inject()(reactiveMongoComponent: ReactiveMongoComponent)
  extends Repo[GreenUtr, String](DB1.toString, reactiveMongoComponent) {
}


class ValidUtrRepoTwo @Inject()(reactiveMongoComponent: ReactiveMongoComponent)
  extends Repo[GreenUtr, String](DB2.toString, reactiveMongoComponent) {
}

class ActiveRepo @Inject()(reactiveMongoComponent: ReactiveMongoComponent)
  extends Repo[ActiveDb, String]("active-repo", reactiveMongoComponent){
  val Id: String = "1" // This collection contains a single element

  def setDb(activeDb: ActiveDb)(implicit ec: ExecutionContext): Future[Unit] = {
    collection.update(Json.obj("_id" -> Id), activeDb, upsert = true).map(_ => ())
      .andThen{
        case Failure(ex) => throw new Exception("Unable to set activeDb ", ex)
      }
  }

  def getActiveDb()(implicit ec: ExecutionContext): Future[CurrentActiveDb] = {
    findById(Id).map {
      _.map(_.activeDb).getOrElse({
        Logger.warn("Unable to read from Active db setting default to DB2")
        DB2})
    }
  }
}


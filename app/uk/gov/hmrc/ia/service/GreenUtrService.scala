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

import javax.inject.Inject
import play.api.libs.json.Json
import uk.gov.hmrc.ia.domain.CurrentActiveDbs.{DB1, DB2}
import uk.gov.hmrc.ia.domain.{ActiveDb, CurrentActiveDb, GreenUtr}
import uk.gov.hmrc.ia.repository.{ActiveRepo, Repo, ValidUtrRepoOne, ValidUtrRepoTwo}

import scala.concurrent.{ExecutionContext, Future}

class GreenUtrService @Inject()(repoOne: ValidUtrRepoOne, repoTwo: ValidUtrRepoTwo, currantActiveDb: ActiveRepo) {
  def uploadBulkInActiveDb(greenListedUtr: List[GreenUtr])(implicit ec: ExecutionContext) = {
    getInActiveDb().flatMap(_.bulkInsert(greenListedUtr)).map(_.totalN)
  }

  def uploadActiveDb(greenListedUtr: List[GreenUtr])(implicit ec: ExecutionContext) = {
    getActiveDb().flatMap(_.bulkInsert(greenListedUtr))
  }

  def count()(implicit ec: ExecutionContext) = {
    for {
      result <- currantActiveDb.getActiveDb()
      repoOne <- repoOne.count
      repoTwo <- repoTwo.count
    } yield DbCount(s"SSTTP is currently pointed to DataBase ${result}",
      s"count DataBase 1 is $repoOne", s"count DataBase 2 is $repoTwo")
  }

  def isGreenUtr(utr: String)(implicit ec: ExecutionContext): Future[Boolean] = {
    getActiveDb.flatMap(_.find("utr" -> utr).map(_.headOption.fold(false)(_ => true)))
  }

  def switchDB()(implicit ec: ExecutionContext): Future[Unit] = {
    currantActiveDb.getActiveDb().map {
      case DB1 => setDb(DB2).map(_ => repoOne.drop)
      case DB2 => setDb(DB1).map(_ => repoTwo.drop)
    }.map(_ => ())
  }

  def dropAll()(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      _ <- repoOne.drop
      _ <- repoTwo.drop
     } yield ()
  }


  def setDb(activeDb: CurrentActiveDb)(implicit ec: ExecutionContext): Future[Unit] = {
    currantActiveDb.setDb(ActiveDb(1, activeDb))
  }

  private def getActiveDb()(implicit ec: ExecutionContext): Future[Repo[GreenUtr, String]] = currantActiveDb.getActiveDb().map {
    case DB1 => repoOne
    case DB2 => repoTwo
  }

  private def getInActiveDb()(implicit ec: ExecutionContext): Future[Repo[GreenUtr, String]] = currantActiveDb.getActiveDb().map {
    case DB1 => repoTwo
    case DB2 => repoOne
  }

}

case class DbCount(dbPointedTo: String, db1: String, db2: String)

object DbCount {
  implicit val formatDbCount = Json.format[DbCount]
}

/*
 * Copyright 2018 HM Revenue & Customs
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
import uk.gov.hmrc.ia.domain.GreenUtr
import uk.gov.hmrc.ia.repository.{CollectionNameRepo, ValidUtrRepo}

import scala.concurrent.{ExecutionContext, Future}
class GreenUtrService @Inject()(repo:ValidUtrRepo,tempRepo:CollectionNameRepo){


  def bulkInsert(greenListedUtr:List[GreenUtr])(implicit ec:ExecutionContext) = {
    tempRepo.bulkInsert(greenListedUtr).map{
      wr => wr.totalN
    }
  }

  def replaceDb()(implicit ec:ExecutionContext):Future[Unit] = {
    for{
    _ <-  tempRepo.collection.rename(repo.collection.db.name,true)
    drop <-  tempRepo.collection.drop(true).map(_ => ())
    }yield drop
  }
  def count()(implicit ec:ExecutionContext) = {
    repo.count
  }

  def isGreenUtr(utr:String)(implicit ec:ExecutionContext):Future[Boolean] = {
    //todo should it even be possable to have more then one of the sma ut perhaps can I turn it into an id?
    repo.find("utr" -> utr).map(_.headOption.fold(false)(_ => true))
  }
}

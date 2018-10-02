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
import uk.gov.hmrc.ia.repository.ValidUtrRepo

import scala.concurrent.{ExecutionContext, Future}
class GreenUtrService @Inject()(repo:ValidUtrRepo) {

  def drop()(implicit ec:ExecutionContext):Future[Unit] = {
    repo.removeAll().map(_ => ())
  }
  def count()(implicit ec:ExecutionContext) = {
    repo.count
  }
  def bulkInsert(greenListedUtr:List[GreenUtr])(implicit ec:ExecutionContext) = {
    repo.bulkInsert(greenListedUtr).map{
      wr => wr.totalN
    }
  }
  def isGreenUtr(utr:String)(implicit ec:ExecutionContext):Future[Boolean] = {
    //todo should it even be possable to have more then one of the sma ut perhaps can I turn it into an id?
    repo.find("utr" -> utr).map(_.headOption.fold(false)(_ => true))
  }
}

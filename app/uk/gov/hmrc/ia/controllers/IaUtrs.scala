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

import com.google.inject.Inject
import play.api.mvc.{Action, AnyContent}
import uk.gov.hmrc.ia.domain.{CurrentActiveDbs, GreenUtr}
import uk.gov.hmrc.ia.service.GreenUtrService
import uk.gov.hmrc.play.bootstrap.controller.BaseController

import scala.concurrent.ExecutionContext.Implicits.global

class IaUtrs @Inject()(service: GreenUtrService) extends BaseController {


  def switch(): Action[AnyContent] = Action.async { implicit request =>
    service.switchDB().map(_ => Ok)
  }

  def dropAll(): Action[AnyContent] = Action.async { implicit request =>
    service.dropAll().map(_ => Ok)
  }

  def setActiveDB(db:String): Action[AnyContent] = Action.async { implicit request =>

    service.setDb(CurrentActiveDbs.withName(db)).map(_ => Ok)
  }

  def count(): Action[AnyContent] = Action.async { implicit request =>
    service.count().map(records => {
      Ok(s"$records")
    })
  }


  def upload(): Action[List[GreenUtr]] = Action.async(parse.json[List[GreenUtr]]) { implicit request =>
    service.uploadBulkInActiveDb(request.body).map(noOfInserts => Ok(s"$noOfInserts"))
  }

  def uploadOne(utr: String): Action[AnyContent] = Action.async { implicit request =>
    service.uploadActiveDb(List(GreenUtr(utr))).map(noOfInserts => Ok(s"$noOfInserts"))
  }

  def find(utr: String) = Action.async { implicit request =>
    service.isGreenUtr(utr).map {
      case true => Ok("")
      case false => NoContent
    }
  }
}

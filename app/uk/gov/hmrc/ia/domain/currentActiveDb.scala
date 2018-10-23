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

package uk.gov.hmrc.ia.domain

import play.api.libs.json.{Format, Json}
import enumeratum._
sealed abstract class CurrentActiveDb extends EnumEntry

object CurrentActiveDbs extends Enum[CurrentActiveDb] {

  case object DB1 extends CurrentActiveDb
  case object DB2 extends CurrentActiveDb

  override def values = findValues

}


case class ActiveDb(activeDb: CurrentActiveDb)

object ActiveDb{

  implicit val dbFormat: Format[CurrentActiveDb] = EnumFormat(CurrentActiveDbs)
  implicit val activeDbFormat = Json.format[ActiveDb]
}

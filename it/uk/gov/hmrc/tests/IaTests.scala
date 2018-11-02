package uk.gov.hmrc.tests

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.support.ItSpec

class IaTests extends ItSpec with BeforeAndAfterEach{
  val iaBaseUrl = "http://localhost:8051"
  val uploadUrl: String = iaBaseUrl + "/ia/upload"
  val switchUrl: String = iaBaseUrl + "/ia/switch"
  val countUrl: String = iaBaseUrl + "/ia/count"
  val findUrlBase: String = iaBaseUrl + "/ia/"

  val utrListJson = Json.arr(
    Json.obj("utr" -> "1234567890"),
    Json.obj("utr" -> "1234567891")
  )

  def uploadUtrs(request: JsValue) = {
    httpClient.POST(uploadUrl, request).futureValue
  }
  def switch() = {
    httpClient.POSTEmpty(switchUrl).futureValue
  }
  def count() = {
    httpClient.GET(countUrl).futureValue
  }


  def find(utr: String) = {
    httpClient.GET(findUrlBase + utr).futureValue
  }

  "client" should {


    "be able to upload the utrs and get how many where updated back" in {
      val resultPost = uploadUtrs(utrListJson)
      resultPost.status shouldBe 200
      resultPost.body shouldBe "2"
    }

    "be able to check if a utr is in the db" in {
      uploadUtrs(utrListJson)
      val findResult = find("1234567890")
      findResult.status shouldBe 200
    }
    "be able to check if a utr not in the db" in {
      uploadUtrs(utrListJson)
      val findResult = find("9999999999")
      findResult.status shouldBe 204
    }

    "be able to drop the db" in {
      uploadUtrs(utrListJson)
      switch()
      val findResult = find("1234567890")
      findResult.status shouldBe 200
    }
    "be able see the number of records in the db in" in {
      uploadUtrs(utrListJson)

      val countResult = count()
      countResult.status shouldBe 200
    }
    //todo write more and better it tests no time to do this sprint
    "be able to switch to another db and drop the other one " in {
      switch()

    }
  }
}

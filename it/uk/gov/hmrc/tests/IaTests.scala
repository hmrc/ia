package uk.gov.hmrc.tests

import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.support.ItSpec

class IaTests extends ItSpec {
  val iaBaseUrl = "http://localhost:8051"
  val uploadUrl: String = iaBaseUrl + "/ia/upload"
  val findUrlBase: String = iaBaseUrl + "/ia/"

  val utrListJson = Json.arr(
    Json.obj("utr" -> "1234567890"),
    Json.obj("utr" -> "1234567890")
  )

  def uploadUtrs(request: JsValue) = {
    httpClient.POST(uploadUrl, request).futureValue
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
      val findResult = find("1234567899")
      findResult.status shouldBe 404
    }
  }
}

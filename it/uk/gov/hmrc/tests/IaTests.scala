package uk.gov.hmrc.tests

import org.scalatest.BeforeAndAfterEach
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.support.ItSpec

class IaTests extends ItSpec with BeforeAndAfterEach{
  val iaBaseUrl = "http://localhost:8051"
  val uploadUrl: String = iaBaseUrl + "/ia/upload"
  val dropUrl: String = iaBaseUrl + "/ia/drop"
  val countUrl: String = iaBaseUrl + "/ia/count"
  val findUrlBase: String = iaBaseUrl + "/ia/"
  val startUrl: String = iaBaseUrl + "/ia/upload/start"

  val utrListJson = Json.arr(
    Json.obj("utr" -> "1234567890"),
    Json.obj("utr" -> "1234567891")
  )

  def uploadUtrs(request: JsValue) = {
    httpClient.POST(uploadUrl, request).futureValue
  }
  def drop() = {
    httpClient.POSTEmpty(dropUrl).futureValue
  }
  def count() = {
    httpClient.GET(countUrl).futureValue
  }

  def start() = {
    httpClient.GET(startUrl).futureValue
  }

  def find(utr: String) = {
    httpClient.GET(findUrlBase + utr).futureValue
  }

  override def beforeEach() {
    drop()
  }
  "client" should {

    "be able create a new file and return the path in" in {
      val resultPost = start()
      resultPost.status shouldBe 200
      resultPost.body shouldBe "tmp/upload.csv"
    }

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
      drop()
      val findResult = find("1234567890")
      findResult.status shouldBe 204
    }
    "be able see the number of records in the db in" in {
      uploadUtrs(utrListJson)

      val countResult = count()
      countResult.status shouldBe 200
      countResult.body.toString shouldBe "Total number of records is 2"
    }
  }
}
